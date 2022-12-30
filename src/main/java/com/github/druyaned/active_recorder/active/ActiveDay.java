package com.github.druyaned.active_recorder.active;

import java.util.ArrayList;

import com.github.druyaned.active_recorder.time.WeekDay;

public final class ActiveDay implements Colored {
    public final int number;
    public final int weekDay;
    private final ArrayList<Activity> dayActivities;
    private volatile long colValByDurSum;
    private volatile long totalDur;
    private ActiveColor color;

    ActiveDay(Activity a) {
        number = a.start.getDay();
        weekDay = WeekDay.get(a.start.getDate());

        dayActivities = new ArrayList<>();
        dayActivities.add(a);
        
        long dur = a.getDuration();
        color = ActiveColor.getBy(a.mode);
        colValByDurSum = color.value * dur;
        totalDur = dur;
    }

    /**
     * Updates the color of the day, adds the activity to day activities and
     * returns a new color value.
     * 
     * @param a an activity to add and to update the day's color.
     * @return a new color value.
     */
    synchronized int updateBy(Activity a) {
        dayActivities.add(a);

        long dur = a.getDuration();
        colValByDurSum += ActiveColor.getBy(a.mode).value * dur;
        totalDur += dur;
        int colVal = (int) (colValByDurSum / totalDur);
        color = ActiveColor.getBy(colVal);

        return colVal;
    }

    /**
     * Activities are stored one by one. Entering a designatioin: {@code id1} - an id
     * of the first element in the day activities. So an index of the activity
     * with ID {@code id} is {@code id - id1}.
     * 
     * @param id an ID of the requested activity.
     * @return the activity with an ID {@code id}.
     */
    public Activity getActivityBy(int id) {
        return dayActivities.get(id - dayActivities.get(0).getId());
    }

    public Activity getActivity(int index) {
        return dayActivities.get(index);
    }

    public Activity getFirstActivity() {
        return dayActivities.get(0);
    }

    public Activity getLastActivity() {
        return dayActivities.get(dayActivities.size() - 1);
    }

    public int getActivitiesSize() {
        return dayActivities.size();
    }

    public int getNumber() { return number; }
    public int getWeekDay() { return weekDay; }

    @Override
    public ActiveColor getColor() {
        return color;
    }
}
