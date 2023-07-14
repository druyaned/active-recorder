package com.github.druyaned.active_recorder.active;

/**
 * Provides an active time of {@link ActiveMode#DEVELOPMENT development},
 * {@link ActiveMode#RELAXATION relaxation} or {@link ActiveMode#STAGNATION stagnation}
 * that meets the <i>requirement</i>: {@code startTime < stopTime}.
 * <p>
 * The class is <i>immutable</i>.
 */
public interface ActiveTime {
    
    public static final int MIN_HOUR = 0;
    public static final int MAX_HOUR = 23;
    public static final int MIN_MINUTE = 0;
    public static final int MAX_MINUTE = 59;
    public static final int MIN_SECOND = 0;
    public static final int MAX_SECOND = 59;
    public static final int MIN_WEEK_DAY = 1;
    public static final int MAX_WEEK_DAY = 7;
    public static final int MIN_MONTH = 1;
    public static final int MAX_MONTH = 12;
    public static final int MONTHS_IN_YEAR = 12;
    public static final int HOURS_IN_DAY = 24;
    public static final int MINUTES_IN_HOUR = 60;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int MILLIS_IN_SECOND = 1000;
    public static final int SECONDS_IN_DAY = SECONDS_IN_MINUTE * MINUTES_IN_HOUR * HOURS_IN_DAY;
    public static final int DAYS_IN_WEEK = 7;
    public static final int MAX_DAY = 31;
    
}
