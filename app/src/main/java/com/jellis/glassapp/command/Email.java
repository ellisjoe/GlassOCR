package com.jellis.glassapp.command;

import android.app.Activity;
import android.content.Intent;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Created by jellis on 6/1/15.
 */
public class Email extends Activity implements Command {

    public Email() {
    }

    @Override
    public String execute(String[] args) {
        String toEmail = args[0];
        String[] mWords = Arrays.copyOfRange(args, 1, args.length);
        String text = StringUtils.join(mWords, " ");

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , toEmail);
        i.putExtra(Intent.EXTRA_SUBJECT, "Test");
        i.putExtra(Intent.EXTRA_TEXT   , text);

        if (!toEmail.equalsIgnoreCase("ellis125@gmail.com")) {
            return "Wrong Email";
        }
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
        return "Email sent.";
    }

    @Override
    public String getCommandText() {
        return "Email";
    }
}
