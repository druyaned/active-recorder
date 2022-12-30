package com.github.druyaned.active_recorder.active;

import com.github.druyaned.active_recorder.time.Month;

public final class ActiveMonth implements Colored {
    public final int number;
    public final int dayAmount;
    private final ActiveDay[] days;
    private final int[] daysByIndex;
    private int daysSize;
    private int colValSum;
    private ActiveColor color;

    ActiveMonth(Activity a) {
        number = a.start.getMonth();
        dayAmount = Month.getDayAmount(a.start.getYear(), number);
        days = new ActiveDay[dayAmount + 1];
        daysByIndex = new int[dayAmount + 1];
        daysSize = 0;

        ActiveDay activeDay = new ActiveDay(a);
        int d = a.start.getDay();
        days[d] = activeDay;
        daysByIndex[daysSize++] = d;
        colValSum = activeDay.getColor().value;
        color = ActiveColor.getBy(colValSum); // colValSum / 1
    }

    /**
     * Updates the color of the month, updates or create an active day by an {@code a} and
     * returns a new color value.
     * 
     * @param a an activity to add and to update the month's color.
     * @return a new color value.
     */
    synchronized int updateBy(Activity a) {
        int d = a.start.getDay();
        
        ActiveDay activeDay = days[d];
        if (activeDay == null) {
            activeDay = new ActiveDay(a);
            days[d] = activeDay;
            daysByIndex[daysSize++] = d;
            colValSum += activeDay.getColor().value;
        } else {
            colValSum -= activeDay.getColor().value;
            colValSum += activeDay.updateBy(a);
        }

        int colVal = colValSum / daysSize;
        color = ActiveColor.getBy(colVal);
        return colVal;
    }

    public ActiveDay getActiveDay(int dayNumber) {
        return days[dayNumber];
    }

    public ActiveDay getActiveDayBy(int index) {
        return days[daysByIndex[index]];
    }

    public ActiveDay getFirstActiveDay() {
        return days[daysByIndex[0]];
    }

    public ActiveDay getLastActiveDay() {
        return days[daysByIndex[daysSize - 1]];
    }

    public int getActiveDaysSize() {
        return daysSize;
    }

    public String getMonthName() {
        return Month.getName(number);
    }

    public int getNumber() { return number; }
    public int getDayAmount() { return dayAmount; }

    @Override
    public ActiveColor getColor() {
        return color;
    }
}
