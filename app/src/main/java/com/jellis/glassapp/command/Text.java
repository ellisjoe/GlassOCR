package com.jellis.glassapp.command;

import android.app.Activity;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jellis on 5/31/15.
 */
public class Text implements Command {

    public static final String ACCOUNT_SID = "AC1c1a0cc37a054fecbfc43ffba8f63e37";
    public static final String AUTH_TOKEN = "46920dda0c7c24e6629837f1958e6833";
    private static final String TAG = "Text";

    public Text() {

    }

    @Override
    public String execute(String[] args) {
        String number = args[0];
        String[] mWords = Arrays.copyOfRange(args, 1, args.length);
        String text = StringUtils.join(mWords, " ");

        Log.v(TAG, "Sending text to " + number);
        Log.v(TAG, "Message: " + text);

        HttpClient httpclient = new DefaultHttpClient();

        HttpPost httppost = new HttpPost(
                "https://api.twilio.com/2010-04-01/Accounts/" + ACCOUNT_SID + "/SMS/Messages");
        String base64EncodedCredentials = "Basic "
                + Base64.encodeToString(
                (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(),
                Base64.NO_WRAP);

        httppost.setHeader("Authorization", base64EncodedCredentials);

        try {

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("From", "+17797044090"));
            nameValuePairs.add(new BasicNameValuePair("To", "+1" + number));
            nameValuePairs.add(new BasicNameValuePair("Body", text));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            Log.v(TAG, "Entity post is: " + EntityUtils.toString(entity));


        } catch (ClientProtocolException e) {

        } catch (IOException e) {

        }

        return "Text sent.";
    }

    @Override
    public String getCommandText() {
        return "text";
    }
}