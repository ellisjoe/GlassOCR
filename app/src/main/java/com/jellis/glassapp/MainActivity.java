package com.jellis.glassapp;

import com.google.android.glass.content.Intents;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.jellis.glassapp.command.CommandRunner;
import com.jellis.glassapp.command.Text;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p/>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 *
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class MainActivity extends Activity {

    private static final int TAKE_PICTURE_REQUEST = 1;

    /**
     * {@link CardScrollView} to use as the main content view.
     */
    private CardScrollView mCardScroller;
    private static final String TAG = "Main";
    private String text = "Tap to take a picture.";
    private CommandRunner cr = new CommandRunner();

    private View mView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.v(TAG, "creating initial view");
        setView(text);
    }

    private void setView(String text) {
        CardBuilder card = new CardBuilder(this, CardBuilder.Layout.TEXT);
        card.setText(text);
        setView(card.getView());
    }

    private void setView(View view) {
        mView = view;

        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(new CardScrollAdapter() {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Object getItem(int position) {
                return mView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return mView;
            }

            @Override
            public int getPosition(Object item) {
                if (mView.equals(item)) {
                    return 0;
                }
                return AdapterView.INVALID_POSITION;
            }
        });

        // Handle the TAP event.
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                takePicture();
            }
        });

        setContentView(mCardScroller);
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
            String thumbnailPath = data.getStringExtra(Intents.EXTRA_THUMBNAIL_FILE_PATH);
            String picturePath = data.getStringExtra(Intents.EXTRA_PICTURE_FILE_PATH);

            // Plays disallowed sound to indicate that TAP actions are not supported.
            //AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            //am.playSoundEffect(Sounds.DISALLOWED);

            // TODO: Show the thumbnail to the user while the full picture is being
            // processed.
            processPictureWhenReady(picturePath);
            //processPictureWhenReady(thumbnailPath);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private String processText(String text) {
        String[] words = text.split(" ");
        String command = words[0];
        String[] args = Arrays.copyOfRange(words, 1, words.length);

        Log.v(TAG, "Command: " + command);
        Log.v(TAG, "Args: " + StringUtils.join(args, " "));

        String response = CommandRunner.run(command, args);

        Log.v(TAG, response);
        return response;
    }


    private void processPictureWhenReady(final String picturePath) {
        final File pictureFile = new File(picturePath);
        setView("Processing Picture...");
        Log.v(TAG, "Attempting to process picture. " + (pictureFile.exists() ? "Ready." : "Not ready..."));

        if (pictureFile.exists()) {
            processImageThreaded(picturePath);
        } else {
            // The file does not exist yet. Before starting the file observer, you
            // can update your UI to let the user know that the application is
            // waiting for the picture (for example, by displaying the thumbnail
            // image and a progress indicator).

            final File parentDirectory = pictureFile.getParentFile();
            FileObserver observer = new FileObserver(parentDirectory.getPath(),
                    FileObserver.CLOSE_WRITE | FileObserver.MOVED_TO) {
                // Protect against additional pending events after CLOSE_WRITE
                // or MOVED_TO is handled.
                private boolean isFileWritten;

                @Override
                public void onEvent(int event, String path) {
                    if (!isFileWritten) {
                        // For safety, make sure that the file that was created in
                        // the directory is actually the one that we're expecting.
                        File affectedFile = new File(parentDirectory, path);
                        isFileWritten = affectedFile.equals(pictureFile);

                        if (isFileWritten) {
                            stopWatching();

                            // Now that the file is ready, recursively call
                            // processPictureWhenReady again
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processPictureWhenReady(picturePath);
                                }
                            });
                        }
                    }
                }
            };
            observer.startWatching();
        }
    }

    private void processImageThreaded(final String picturePath) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                processImage(picturePath);
            }
        });
        t.start();
    }

    private void processImage(final String picturePath) {
        String text = TessaractOCR.processPicture(picturePath);
        text = "Text 8153882890 Hi";
        final String response = processText(text);
        Log.v(TAG, "Response: " + response);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.v(TAG, "Setting view to: " + response);
                setView(response);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        super.onPause();
    }

}
