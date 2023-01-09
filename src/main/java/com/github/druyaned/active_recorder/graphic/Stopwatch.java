package com.github.druyaned.active_recorder.graphic;

import static com.github.druyaned.active_recorder.active.ActiveTime.*;
import java.awt.event.*;
import javax.swing.*;
import com.github.druyaned.active_recorder.active.*;
import java.time.Instant;

public class Stopwatch {
    public static final int DELAY = 256;

    private final Timer timer;
    private final StopwatchTasks stopwatchTasks;
    private Instant t1 = null;
    
    /**
     * Creates a new stopwatch to record {@link activity activities}.
     * 
     * @param tasks provides beginning and ending setups of this stopwatch.
     */
    Stopwatch(StopwatchTasks tasks) {
        ActionListener stopwatchBeginTask = (e) -> {
            long duration = Instant.now().getEpochSecond() - t1.getEpochSecond();
            int dayAsSeconds = (int) (duration % SECONDS_IN_DAY);
            int dayAsMinutes = dayAsSeconds / SECONDS_IN_MINUTE;
            int h = (int) (duration / (SECONDS_IN_MINUTE * MINUTES_IN_HOUR));
            int m = dayAsMinutes % MINUTES_IN_HOUR;
            int s = dayAsSeconds % SECONDS_IN_MINUTE;
            String text = String.format(ControlPanel.STOPWATCH_FORMAT, h, m, s);
            tasks.getTimerDisplay().setText(text);
        };
        this.timer = new Timer(DELAY, stopwatchBeginTask);
        this.stopwatchTasks = tasks;
    }

    /**
     * Returns the starting time of the {@link Activity activity}
     * which can be a {@code null} if the stopwatch is stopped.
     * 
     * @return the starting time of the activity or a {@code null} if the stopwatch is stopped.
     */
    public Instant getStartDateTime() { return t1; }

    void start() {
        t1 = Instant.now();
        stopwatchTasks.runAllBeginTasks();
        timer.start();
    }

    void startFrom(Instant t1) {
        if (t1 == null) {
            throw new NullPointerException("can't start the stopwatch from the null");
        }
        if (t1.compareTo(Instant.now()) > 0) {
            throw new IllegalArgumentException("can't start the stopwatch from the future");
        }

        this.t1 = t1;
        stopwatchTasks.runAllBeginTasks();
        timer.start();
    }

    /**
     * Stops the stopwatch, sets the {@code starting time to null} and returns
     * {@link ActiveTime active time} by the chosen settings.
     * 
     * @param mode chosen {@link ActiveMode active mode} of the {@link Activity activity}.
     * @param descr chosen description of the activity.
     */
    Activity stop(ActiveMode mode, String descr) {
        Instant t1Copy = t1;
        t1 = null;
        Instant t2 = Instant.now();
        timer.stop();
        stopwatchTasks.runAllEndTasks();
        String text = String.format(ControlPanel.STOPWATCH_FORMAT, 0, 0, 0);
        stopwatchTasks.getTimerDisplay().setText(text);
        if (t1Copy.getEpochSecond() == t2.getEpochSecond()) {
            return null;
        } else {
            return new Activity(t1Copy, t2, mode, descr);
        }
    }

    public boolean started() { return t1 != null; }
}
