package com.github.druyaned.active_recorder.active;

import com.github.druyaned.active_recorder.time.Date;
import com.github.druyaned.active_recorder.time.DateTime;

/**
 * Provides an activity of {@link ActiveTime active time}
 * which meets an additional <i>requirement</i>:
 * <code>(stop.time == 00:00:00 && stop.date - start.date == 1 day) ||
 * (start.date == stop.date)</code>.
 * <p>
 * The class is <i>mutable</i>.
 */
public class Activity extends ActiveTime {    
    public static boolean meetsRequirements(ActiveTime activeTime) {
        Date startDate = activeTime.start.getDate(), stopDate = activeTime.stop.getDate();
        
        boolean minStopTime = (activeTime.stop.getDaySeconds() == DateTime.MIN_SECOND);
        boolean oneDayDiff = (stopDate.rawDays - startDate.rawDays == 1);
        boolean equlDates = startDate.equals(stopDate);

        if ((minStopTime && oneDayDiff) || equlDates) {
            return true;
        }
        return false;
    }

//-Non-static---------------------------------------------------------------------------------------

    private volatile int id;

    /**
     * Creates a new activity with empty description which meets an additional <i>requirement</i>:
     * <code>(stop.time == 00:00:00 && stop.date - start.date == 1 day) ||
     * (start.date == stop.date)</code>.
     * 
     * @param start starting {@link DateTime date time} of the activity.
     * @param stop stopping {@link DateTime date time} of the activity.
     * @param mode {@link ActiveMode active mode} of the activity.
     * @param id an identifier in {@link Activities activities}.
     */
    Activity(DateTime start, DateTime stop, ActiveMode mode, String descr, int id) {
        super(start, stop, mode, descr);
        this.id = id;
        
        if (!meetsRequirements(this)) {
            throw new IllegalArgumentException("invalid activeTime " + super.toString());
        }
    }

    /**
     * Creates a new activity with empty description which meets an additional <i>requirement</i>:
     * <code>(stop.time == 00:00:00 && stop.date - start.date == 1 day) ||
     * (start.date == stop.date)</code>.
     * 
     * @param activeTime to makes a copy from its fields.
     * @param id an identifier in {@link Activities activities}.
     */
    Activity(ActiveTime activeTime, int id) {
        super(activeTime.start, activeTime.stop, activeTime.mode, activeTime.descr);
        this.id = id;
        
        if (!meetsRequirements(this)) {
            throw new IllegalArgumentException("invalid activeTime " + super.toString());
        }
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        Activity o = (Activity) other;
        return start.equals(o.start) && stop.equals(o.stop) && mode.equals(o.mode);
    }

    @Override
    public String toString() {
        return "[start=" + start + ", stop=" + stop + ", mode=" + mode +
            ", description=" + descr +  ", id=" + id + "]";
    }
}
