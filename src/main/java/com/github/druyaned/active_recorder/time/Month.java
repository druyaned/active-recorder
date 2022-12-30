package com.github.druyaned.active_recorder.time;

import static com.github.druyaned.active_recorder.time.Date.*;

public class Month {

    private static final String[] names = {
        null,
        "January", "February", "March", "April", "May", "June", "July",
        "August", "September", "October", "November", "December"
    };

    private static final int[] dayAmountsLeap = {
        0,
        31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
    };

    private static final int[] dayAmountsNotLeap = {
        0,
        31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
    };

    /**
     * Retuns a name of the month.
     * 
     * @param month from {@link Date#MIN_MONTH} to {@link Date#MAX_MONTH};
     *              if {@code month = 0} returns {@code null}.
     * @return a name of the month.
     */
    public static String getName(int month) {
        return names[month];
    }

    /**
     * Checks the year and the month by the limitations
     * {@link Date#MIN_YEAR min year}, {@link Date#MAX_YEAR max year},
     * {@link Date#MIN_MONTH min month}, {@link Date#MAX_MONTH max month}
     * and returns a day amount of the month.
     * 
     * @param year to {@link Date#isLeap(int) find out} is it leap or not;
     *        can be from {@link Date#MIN_YEAR} to {@link Date#MAX_YEAR}.
     * @param month from {@link Date#MIN_MONTH} to {@link Date#MAX_MONTH}.
     * @return a {@code day amount} of the {@code month}.
     */
    public static int getDayAmount(int year, int month) {
        if (year < MIN_YEAR || year > MAX_YEAR)
            throw new IllegalArgumentException("invalid year");
        if (month < MIN_MONTH || month > MAX_MONTH)
            throw new IllegalArgumentException("invalid month");

        if (isLeap(year))
            return dayAmountsLeap[month];

        return dayAmountsNotLeap[month];
    }

    /** Fast getter without any verifications. */
    protected static int getDayAmountLeap(int month) {
        return dayAmountsLeap[month];
    }

    /** Fast getter without any verifications. */
    protected static int getDayAmountNotLeap(int month) {
        return dayAmountsNotLeap[month];
    }
}
