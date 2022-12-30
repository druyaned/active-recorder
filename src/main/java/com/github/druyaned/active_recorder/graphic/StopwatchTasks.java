package com.github.druyaned.active_recorder.graphic;

import java.util.ArrayList;

import javax.swing.JLabel;

/**
 * The class provides beginning and ending setups for the {@link Stopwatch}.
 */
public class StopwatchTasks {
    private final JLabel stopwatchDisplay;
    private final ArrayList<Runnable> beginTasks;
    private final ArrayList<String> beginTaskNames;
    private final ArrayList<Runnable> endTasks;
    private final ArrayList<String> endTaskNames;

    public StopwatchTasks(JLabel stopwatchDisplay) {
        this.stopwatchDisplay = stopwatchDisplay;
        beginTasks = new ArrayList<>();
        beginTaskNames = new ArrayList<>();
        endTasks = new ArrayList<>();
        endTaskNames = new ArrayList<>();
    }

    JLabel getTimerDisplay() {
        return stopwatchDisplay;
    }

    /**
     * Adds the task that will be run befor {@link #actionPerformed stopwatch startup}.
     * 
     * @param task that will be run befor {@link #actionPerformed stopwatch startup}.
     * @param name a short description of the task.
    */
    public boolean addBeginTask(Runnable task, String name) {
        boolean added = beginTasks.add(task);
        if (added) {
            beginTaskNames.add(name);
        }
        return added;
    }

    /**
     * Adds the task that will be run after {@link #stopTimer stopwatch stopping}.
     * 
     * @param task that will be run after {@link #stopTimer stopwatch stopping}.
     * @param name a short description of the task.
    */
    public boolean addEndTask(Runnable task, String name) {
        boolean added = endTasks.add(task);
        if (added) {
            endTaskNames.add(name);
        }
        return added;
    }

    public void runAllBeginTasks() {
        for (Runnable beginTask : beginTasks) {
            beginTask.run();
        }
    }

    public void runAllEndTasks() {
        for (Runnable endTask : endTasks) {
            endTask.run();
        }
    }
}
