package com.github.druyaned.active_recorder.data;

import com.github.druyaned.active_recorder.active.ActiveMode;
import java.time.Instant;

public class StartData {
    public static final String STOPWATCH_STARTED_KEY = "stopwatch.started";
    public static final String START_TIME_KEY = "start.time";
    public static final String START_MODE_KEY = "start.mode";
    public static final String START_DESCRIPTION_KEY = "start.descr";

    public final Instant time;
    public final ActiveMode mode;
    public final String descr;

    public StartData(Instant time, ActiveMode mode, String descr) {
        this.time = time;
        this.mode = mode;
        this.descr = descr;
    }
}
