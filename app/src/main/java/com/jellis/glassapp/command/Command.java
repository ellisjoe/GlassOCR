package com.jellis.glassapp.command;

import org.reflections.Reflections;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by jellis on 5/31/15.
 */
public interface Command {
    public String execute(String[] args);
    public String getCommandText();
}
