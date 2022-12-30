package com.github.druyaned.active_recorder.active;

import static com.github.druyaned.active_recorder.time.Date.MONTHS_IN_YEAR;

public final class ActiveYear implements Colored {
    public final int number;
    private final ActiveMonth[] months;
    private final int[] monthsByIndex;
    private int monthsSize;
    private volatile int colValSum;
    private ActiveColor color;

    public ActiveYear(Activity a) {
        this.number = a.start.getYear();
        months = new ActiveMonth[MONTHS_IN_YEAR + 1];
        monthsByIndex = new int[MONTHS_IN_YEAR + 1];
        monthsSize = 0;

        ActiveMonth activeMonth = new ActiveMonth(a);
        int m = a.start.getMonth();
        months[m] = activeMonth;
        monthsByIndex[monthsSize++] = m;

        colValSum = activeMonth.getColor().value;
        color = ActiveColor.getBy(colValSum); // colValSum / 1
    }

    /**
     * Updates the color of the year, updates or create an active month by an {@code a} and
     * returns a new color value.
     * 
     * @param a an activity to add and to update the year's color.
     * @return a new color value.
     */
    synchronized int updateBy(Activity a) {
        int m = a.start.getMonth();
        
        ActiveMonth activeMonth = months[m];
        if (activeMonth == null) {
            activeMonth = new ActiveMonth(a);
            months[m] = activeMonth;
            monthsByIndex[monthsSize++] = m;
            colValSum += activeMonth.getColor().value;
        } else {
            colValSum -= activeMonth.getColor().value;
            colValSum += activeMonth.updateBy(a);
        }

        int colVal = colValSum / monthsSize;
        color = ActiveColor.getBy(colVal);
        return colVal;
    }

    public ActiveMonth getActiveMonth(int monthNumber) {
        return months[monthNumber];
    }

    public ActiveMonth getActiveMonthBy(int index) {
        return months[monthsByIndex[index]];
    }

    public ActiveMonth getFirstActiveMonth() {
        return months[monthsByIndex[0]];
    }

    public ActiveMonth getLastActiveMonth() {
        return months[monthsByIndex[monthsSize - 1]];
    }

    public int getActiveMonthsSize() {
        return monthsSize;
    }

    public int getNumber() { return number; }

    @Override
    public ActiveColor getColor() {
        return color;
    }
}
