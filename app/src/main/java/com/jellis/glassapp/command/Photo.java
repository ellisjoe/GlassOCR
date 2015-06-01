package com.jellis.glassapp.command;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

/**
 * Created by jellis on 6/1/15.
 */
public class Photo extends Activity implements Command {

    private static final int TAKE_PICTURE_REQUEST = 1;

    @Override
    public String execute(String[] args) {
        Long wait = Long.parseLong(args[0]);
        try {
            Thread.sleep(wait);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "Failed";
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE_REQUEST);
        return "Photo Taken";
    }

    @Override
    public String getCommandText() {
        return "Photo";
    }
}
