package com.github.druyaned.active_recorder.active;

import com.github.druyaned.active_recorder.time.DateTime;

/**
 * Provides an active time of {@link ActiveMode#DEVELOPMENT development},
 * {@link ActiveMode#RELAXATION relaxation} or {@link ActiveMode#STAGNATION stagnation}
 * that meets the <i>requirement</i>: {@code startTime < stopTime}.
 * <p>
 * The class is <i>immutable</i>.
 */
public class ActiveTime {
    public static final int MAX_DESCRIPTION_LENGTH = 48;

    public static boolean meetsRequirements(DateTime start, DateTime stop) {
        return start.compareTo(stop) < 0;
    }

//-Non-static---------------------------------------------------------------------------------------

    protected final DateTime start;
    protected final DateTime stop;
    protected final ActiveMode mode;
    protected final String descr;

    /**
     * Acreates an active time of {@link ActiveMode#DEVELOPMENT development},
     * {@link ActiveMode#RELAXATION relaxation} or {@link ActiveMode#STAGNATION stagnation}
     * that meets the <i>requirement</i>: {@code startTime < stopTime}.
     * 
     * @param start starting {@link DateTime date time} of the activity.
     * @param stop stopping {@link DateTime date time} of the activity.
     * @param mode {@link ActiveMode active mode} of the activity.
     */
    public ActiveTime(DateTime start, DateTime stop, ActiveMode mode) {
        if (start.compareTo(stop) >= 0) {
            throw new IllegalArgumentException("startTime >= stopTime: " + start + " >= " + stop);
        }
        this.start = start;
        this.stop = stop;
        this.mode = mode;
        this.descr = "";
    }

    /**
     * Acreates an active time of {@link ActiveMode#DEVELOPMENT development},
     * {@link ActiveMode#RELAXATION relaxation} or {@link ActiveMode#STAGNATION stagnation}
     * that meets the <i>requirement</i>: {@code startTime < stopTime}.
     * 
     * @param start starting {@link DateTime date time} of the activity.
     * @param stop stopping {@link DateTime date time} of the activity.
     * @param mode {@link ActiveMode active mode} of the activity.
     * @param descr a short description of the activity
     *        which must have a length less than {@link #MAX_DESCRIPTION_LENGTH maximum length}.
     */
    public ActiveTime(DateTime start, DateTime stop, ActiveMode mode, String descr) {
        if (start.compareTo(stop) >= 0) {
            throw new IllegalArgumentException("startTime >= stopTime: " + start + " >= " + stop);
        }
        int l = descr.length();
        if (l > MAX_DESCRIPTION_LENGTH) {
            descr = descr.substring(0, MAX_DESCRIPTION_LENGTH);
        }
        this.start = start;
        this.stop = stop;
        this.mode = mode;
        this.descr = descr;
    }

    /**
     * Gives a duration in {@code seconds} of the active time.
     * 
     * @return a duration in {@code seconds} of the active time.
     */
    public long getDuration() {
        return stop.rawSeconds - start.rawSeconds;
    }

    public DateTime getStart() {
        return start;
    }

    public DateTime getStop() {
        return stop;
    }

    public ActiveMode getMode() {
        return mode;
    }

    public String getDescription() {
        return descr;
    }

    @Override
    public String toString() {
        return "[start=" + start + ", stop=" + stop + ", mode=" + mode +
            ", description=" + descr + "]";
    }
}
