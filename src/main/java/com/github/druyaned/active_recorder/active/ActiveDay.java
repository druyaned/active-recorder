package com.github.druyaned.active_recorder.active;

import java.util.ArrayList;

public final class ActiveDay implements Colored {
    public final int number;
    public final int weekDay;
    private final ArrayList<ZonedActivity> dayZonedActivities;
    private volatile long colValByDurSum;
    private volatile long totalDur;
    private ActiveColor color;
    
    ActiveDay(ZonedActivity a) {
        number = a.getZonedStart().getDayOfMonth();
        weekDay = a.getZonedStart().getDayOfWeek().getValue();
        dayZonedActivities = new ArrayList<>();
        dayZonedActivities.add(a);
        long dur = a.duration().getSeconds();
        color = ActiveColor.getBy(a.getMode());
        colValByDurSum = color.value * dur;
        totalDur = dur;
    }

    /**
     * Updates the color of the day, adds the activity to day activities and
     * returns a new color value.
     * 
     * @param za an activity to add and to update the day's color.
     * @return a new color value.
     */
    synchronized int updateBy(ZonedActivity za) {
        dayZonedActivities.add(za);
        long dur = za.duration().getSeconds();
        colValByDurSum += ActiveColor.getBy(za.getMode()).value * dur;
        totalDur += dur;
        int colVal = (int) (colValByDurSum / totalDur);
        color = ActiveColor.getBy(colVal);
        return colVal;
    }
    
    public ZonedActivity getZonedActivity(int index) { return dayZonedActivities.get(index); }

    public ZonedActivity getFirstZonedActivity() { return dayZonedActivities.get(0); }

    public ZonedActivity getLastZonedActivity() {
        return dayZonedActivities.get(dayZonedActivities.size() - 1);
    }

    public int getZonedActivitiesSize() { return dayZonedActivities.size(); }

    public int getNumber() { return number; }
    
    public int getWeekDay() { return weekDay; }

    @Override
    public ActiveColor getColor() { return color; }
}
