package com.jellis.glassapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Created by jellis on 6/1/15.
 */
public class TessaractOCR {
    private static final String TAG = "OCR";
    private static TessBaseAPI baseApi = new TessBaseAPI();
    private static BitmapFactory.Options options = new BitmapFactory.Options();
    private static String DATA_PATH = "/sdcard/";

    private static String recognizedText;

    static {
        options.inSampleSize = 2;
        options.inDensity = 600;

        baseApi.init(DATA_PATH, "eng");
        baseApi.setDebug(true);
        baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);
        baseApi.setVariable("language_model_penalty_non_dict_word", "0.95");
        baseApi.setVariable("tessedit_char_whitelist", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }

    private TessaractOCR () {

    }

    public static String processPicture(final String picturePath) {
        //final File pictureFile = new File(picturePath);

        Bitmap bitmap = BitmapFactory.decodeFile(picturePath, options);
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        baseApi.setImage(bitmap);

        recognizedText = baseApi.getUTF8Text();
        Log.v(TAG, StringUtils.join(baseApi.wordConfidences(), ' '));
        Log.v(TAG, "Text: " + recognizedText);

        return recognizedText;
    }
}
