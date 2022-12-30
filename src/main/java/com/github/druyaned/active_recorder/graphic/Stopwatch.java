package com.github.druyaned.active_recorder.graphic;

import static com.github.druyaned.active_recorder.time.DateTime.*;

import java.awt.event.*;

import javax.swing.*;

import com.github.druyaned.active_recorder.active.*;
import com.github.druyaned.active_recorder.time.DateTime;

public class Stopwatch {
    public static final int DELAY = 500;

    private final Timer timer;
    private final StopwatchTasks stopwatchTasks;
    private DateTime t1 = null;

    /**
     * Creates a new stopwatch to record {@link activity activities}.
     * 
     * @param tasks provides beginning and ending setups of this stopwatch.
     */
    Stopwatch(StopwatchTasks tasks) {
        ActionListener stopwatchBeginTask = (e) -> {
            long duration = DateTime.now().rawSeconds - t1.rawSeconds;

            int dayAsSeconds = (int) (duration % SECONDS_IN_DAY);
            int dayAsMinutes = dayAsSeconds / SECONDS_IN_MINUTE;
            int h = (int) (duration / (SECONDS_IN_MINUTE * MINUTES_IN_HOUR));
            int m = dayAsMinutes % MINUTES_IN_HOUR;
            int s = dayAsSeconds % SECONDS_IN_MINUTE;

            String text = String.format(ControlPanel.STOPWATCH_DISPLAY_FORMAT, h, m, s);
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
    public DateTime getStartDateTime() {
        return t1;
    }

    void start() {
        t1 = DateTime.now();
        stopwatchTasks.runAllBeginTasks();
        timer.start();
    }

    void startFrom(DateTime t1) {
        if (t1 == null) {
            throw new NullPointerException("can't start the stopwatch from the null");
        }
        if (t1.compareTo(DateTime.now()) > 0) {
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
    ActiveTime stop(ActiveMode mode, String descr) {
        DateTime t1Copy = t1;
        t1 = null;
        DateTime t2 = DateTime.now();
        timer.stop();
        stopwatchTasks.runAllEndTasks();
        String text = String.format(ControlPanel.STOPWATCH_DISPLAY_FORMAT, 0, 0, 0);
        stopwatchTasks.getTimerDisplay().setText(text);
        if (t1Copy.rawSeconds == t2.rawSeconds) {
            return null;
        } else {
            return new ActiveTime(t1Copy, t2, mode, descr);
        }
    }

    public boolean started() {
        return t1 != null;
    }
}
