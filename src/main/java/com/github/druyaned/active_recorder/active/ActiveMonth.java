package com.github.druyaned.active_recorder.active;

import java.time.Month;
import java.time.ZonedDateTime;

public final class ActiveMonth implements Colored {
    public final int number;
    public final int dayAmount;
    private final ActiveDay[] days;
    private final int[] daysByIndex;
    private int daysSize;
    private int colValSum;
    private ActiveColor color;

    ActiveMonth(ZonedActivity za) {
        ZonedDateTime z1 = za.getZonedStart();
        number = z1.getMonthValue();
        dayAmount = z1.getMonth().length(z1.toLocalDate().isLeapYear());
        days = new ActiveDay[dayAmount + 1];
        daysByIndex = new int[dayAmount + 1];
        daysSize = 0;
        
        ActiveDay activeDay = new ActiveDay(za);
        int d = z1.getDayOfMonth();
        days[d] = activeDay;
        daysByIndex[daysSize++] = d;
        colValSum = activeDay.getColor().value;
        color = ActiveColor.getBy(colValSum); // colValSum / 1
    }

    /**
     * Updates the color of the month, updates or create an active day by an {@code a} and
     * returns a new color value.
     * 
     * @param za an activity to add and to update the month's color.
     * @return a new color value.
     */
    synchronized int updateBy(ZonedActivity za) {
        int d = za.getZonedStart().getDayOfMonth();
        ActiveDay activeDay = days[d];
        if (activeDay == null) {
            activeDay = new ActiveDay(za);
            days[d] = activeDay;
            daysByIndex[daysSize++] = d;
            colValSum += activeDay.getColor().value;
        } else {
            colValSum -= activeDay.getColor().value;
            colValSum += activeDay.updateBy(za);
        }

        int colVal = colValSum / daysSize;
        color = ActiveColor.getBy(colVal);
        return colVal;
    }

    public ActiveDay getActiveDay(int dayNumber) { return days[dayNumber]; }

    public ActiveDay getActiveDayBy(int index) { return days[daysByIndex[index]]; }

    public ActiveDay getFirstActiveDay() { return days[daysByIndex[0]]; }

    public ActiveDay getLastActiveDay() { return days[daysByIndex[daysSize - 1]]; }

    public int getActiveDaysSize() { return daysSize; }

    public String getMonthName() { return Month.of(number).toString(); }

    public int getNumber() { return number; }
    
    public int getDayAmount() { return dayAmount; }

    @Override
    public ActiveColor getColor() { return color; }
}
