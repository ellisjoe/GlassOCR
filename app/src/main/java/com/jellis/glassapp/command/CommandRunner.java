package com.jellis.glassapp.command;

import android.util.Log;

import com.jellis.glassapp.command.Command;

import org.reflections.Reflections;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by jellis on 6/1/15.
 */
public class CommandRunner {

    private static final String TAG = "JOE";
    private static Command[] commands = { new Text(), new Email() };

    public CommandRunner() {
    }

    public static String run(String command, String[] args) {
        String response = "Uknown command: " + command;
        if (true) {
            return commands[1].execute(new String[]{"ellis125@gmail.com", "Hi"});
        }
        for (Command c : commands) {
            if (c.getCommandText().equalsIgnoreCase(command)) {
                try {
                    response = c.execute(args);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.v(TAG, "Failed: " + e.getCause());
                    response = "Failed: " + e.getCause();
                }
                break;
            }
        }
        return response;
    }
}
