package com.github.druyaned.active_recorder.active;

import java.time.Duration;
import java.time.Instant;

/**
 * Provides an activity of {@link ActiveTime active time}
 * which meets an additional <i>requirement</i>:
 * <code>(stop.time == 00:00:00 && stop.date - start.date == 1 day) ||
 * (start.date == stop.date)</code>.
 * <p>
 * The class is <i>mutable</i>.
 */
public class Activity {
    public static final int MAX_DESCR_LENGTH = 48;
    
    private final Instant start;
    private final Instant stop;
    private final ActiveMode mode;
    private volatile String descr;
    
    /**
     * Creates a new activity with empty description which meets an additional <i>requirement</i>:
     * <code>(stop.time == 00:00:00 && stop.date - start.date == 1 day) ||
     * (start.date == stop.date)</code>.
     * 
     * @param start starting {@link DateTime date time} of the activity.
     * @param stop stopping {@link DateTime date time} of the activity.
     * @param mode {@link ActiveMode active mode} of the activity.
     * @param descr a short description of the activity.
     * @see #MAX_DESCR_LENGTH
     */
    public Activity(Instant start, Instant stop, ActiveMode mode, String descr) {
        long startSec = start.toEpochMilli() / 1000;
        long stopSec = stop.toEpochMilli() / 1000;
        if (startSec >= stopSec) {
            String t1 = start.toString(), t2 = stop.toString();
            throw new IllegalArgumentException("start >= stop: " + t1 + " >= " + t2);
        }
        if (descr.length() >= MAX_DESCR_LENGTH) {
            throw new IllegalArgumentException("descr.length = " + descr.length() +
                                               " >= " + MAX_DESCR_LENGTH);
        }
        this.start = start;
        this.stop = stop;
        this.mode = mode;
        this.descr = descr;
    }
    
//-Getters-and-Setters------------------------------------------------------------------------------
    
    public Instant getStart() { return start; }
    
    public Instant getStop() { return stop; }
    
    public ActiveMode getMode() { return mode; }
    
    public String getDescr() { return descr; }
    
//-Methods------------------------------------------------------------------------------------------
    
    /**
     * Gives a duration in {@code seconds} of the active time.
     * 
     * @return a duration in {@code seconds} of the active time.
     */
    public Duration duration() { return Duration.between(start, stop); }
    
    @Override
    public String toString() {
        return "[start=" + start + ", stop=" + stop + ", mode=" + mode + ", descr=" + descr + "]";
    }
}
