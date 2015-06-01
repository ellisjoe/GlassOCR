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
    private static final Command[] commands = { new Text() };

    private CommandRunner() {
    }

    public static String run(String command, String[] args) {
        String response = "Uknown command: " + command;
        for (Command c : commands) {
            if (c.getCommandText().equalsIgnoreCase(command)) {
                response = c.execute(args);
                break;
            }
        }
        return response;
    }
}
