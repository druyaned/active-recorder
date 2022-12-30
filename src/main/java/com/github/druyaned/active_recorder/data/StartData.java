package com.github.druyaned.active_recorder.data;

import com.github.druyaned.active_recorder.active.ActiveMode;

public class StartData {
    public static final String STOPWATCH_STARTED_KEY = "stopwatch.started";
    public static final String START_RAWSECONDS_KEY = "start.rawSeconds";
    public static final String START_MODE_KEY = "start.mode";
    public static final String START_DESCRIPTION_KEY = "start.descr";

    public final long startRawSeconds;
    public final ActiveMode mode;
    public final String descr;

    public StartData(long startRawSeconds, ActiveMode mode, String descr) {
        this.startRawSeconds = startRawSeconds;
        this.mode = mode;
        this.descr = descr;
    }
}
