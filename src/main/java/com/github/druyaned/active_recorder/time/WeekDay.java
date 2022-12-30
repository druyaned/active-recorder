package com.github.druyaned.active_recorder.time;

import static com.github.druyaned.active_recorder.time.Date.*;

public class WeekDay {

    /** Corresponds to Monday. */
    public static final int MIN_VALUE = 1;

    /** Corresponds to Sunday. */
    public static final int MAX_VALUE = 7;

    private static final String[] shortNames = {
        null, "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
    };

    private static final String[] names = {
        null, "Monday", "Tuesday", "Wednesday",
        "Thursday", "Friday", "Saturday", "Sunday"
    };

    /**
     * Returns a short name of the day in a week.
     * 
     * @param value from {@link #MIN_VALUE} to {@link #MAX_VALUE};
     *        if {@code value = 0} returns {@code null}.
     * @return a short name in 3 letters (like <i>Mon</i>).
     */
    public static String getShortName(int value) {
        return shortNames[value];
    }

    /**
     * Returns a name of the day in a week.
     * 
     * @param value from {@link #MIN_VALUE} to {@link #MAX_VALUE};
     *        if {@code value = 0} returns {@code null}.
     * @return a name of the day in a week.
     */
    public static String getName(int value) {
        return names[value];
    }

    /**
     * Checks the year and the month by the limitations
     * {@link Date#MIN_YEAR min year}, {@link Date#MAX_YEAR max year},
     * {@link Date#MIN_MONTH min month}, {@link Date#MAX_MONTH max month}
     * and returns a value of the day in a week
     * from {@link #MIN_VALUE monday} to {@link #MAX_VALUE sunday}.
     * <p>
     * <i>NOTE</i>: compatible with {@link #getName(int)}.
     * 
     * @param year a year of the day from {@link Date#MIN_YEAR min year}
     *        to {@link Date#MAX_YEAR max year}.
     * @param month from {@link Date#MIN_MONTH min month}
     *        to {@link Date#MAX_MONTH max month}.
     * @param day from {@link Date#MIN_DAY min day value}
     *        to {@link Month#getDayAmount day amount}.
     * @return a value of the day in a week
     *        from {@link #MIN_VALUE} to {@link #MAX_VALUE}.
     */
    public static int get(int year, int month, int day) {
        if (year < MIN_YEAR || year > MAX_YEAR) {
            throw new IllegalArgumentException("invalid year " + year);
        }
        if (month < MIN_MONTH || month > MAX_MONTH) {
            throw new IllegalArgumentException("invalid month " + month);
        }

        int weekDayOffset;
        boolean leapYear = isLeap(year);
        int dayAmount;
        if (leapYear) {
            dayAmount = Month.getDayAmountLeap(month);
        } else {
            dayAmount = Month.getDayAmountNotLeap(month);
        }
        if (day < MIN_DAY || day > dayAmount) {
            throw new IllegalArgumentException("invalid day " + day);
        }

        if (leapYear) {
            weekDayOffset = weekDayOffsetLeap[month][day];
        } else {
            weekDayOffset = weekDayOffsetNotLeap[month][day];
        }

        int weekDay = weekDayOffset + firstWeekDayInPeriodYear[year % LEAP_PERIOD];
        return ((weekDay - MIN_VALUE) % DAYS_IN_WEEK) + MIN_VALUE;
    }

    public static int get(Date date) {
        int weekDayOffset;
        if (isLeap(date.year)) {
            weekDayOffset = weekDayOffsetLeap[date.month][date.day];
        } else {
            weekDayOffset = weekDayOffsetNotLeap[date.month][date.day];
        }

        int weekDay = weekDayOffset + firstWeekDayInPeriodYear[date.year % LEAP_PERIOD];
        return ((weekDay - MIN_VALUE) % DAYS_IN_WEEK) + MIN_VALUE;
    }

    /**
     * Returns a value from {@link #MIN_VALUE min week day} to {@link #MAX_VALUE max week day}
     * of the first day in the {@code month} of the {@code year}.
     * 
     * @param year to define a correct value.
     * @param month to define a correct value.
     * @return a value from {@link #MIN_VALUE min week day} to {@link #MAX_VALUE max week day}
     *         of the first day in the {@code month} of the {@code year}.
     */
    public static int getFirstWeekDayIn(int year, int month) {
        if (year < MIN_YEAR || year > MAX_YEAR) {
            throw new IllegalArgumentException("invalid year " + year);
        }
        if (month < MIN_MONTH || month > MAX_MONTH) {
            throw new IllegalArgumentException("invalid month " + month);
        }

        int weekDayOffset;
        if (isLeap(year)) {
            weekDayOffset = weekDayOffsetLeap[month][MIN_DAY];
        } else {
            weekDayOffset = weekDayOffsetNotLeap[month][MIN_DAY];
        }

        int weekDay = weekDayOffset + firstWeekDayInPeriodYear[year % LEAP_PERIOD];
        return ((weekDay - MIN_VALUE) % DAYS_IN_WEEK) + MIN_VALUE;
    }

    public static int getFirstWeekDayIn(int year) {
        if (year < MIN_YEAR || year > MAX_YEAR) {
            throw new IllegalArgumentException("invalid year " + year);
        }

        return firstWeekDayInPeriodYear[year % LEAP_PERIOD];
    }

    /**
     * Returns a {@link #getWeekDay vlue} of the {@code first week day}
     * in a <i>period year</i>.
     * <p>
     * <i>Period year</i> - a year from the {@link #LEAP_PERIOD} where the
     * first year is {@code 1}, corresponds to the {@code first year of AD}
     * and starts from {@link #MIN_VALUE monday}.
     * 
     * @param year to get a value of its first day.
     *        <b>WARNING</b>: must be {@code positive}.
     * @return year's first day offset from {@link Date#MIN_DAY min day value}
     *         to {@link Date#MAX_DAY max day value}.
     */
    protected static int getWeekDayInPeriodYear(int year) {
        return firstWeekDayInPeriodYear[year % LEAP_PERIOD];
    }

    /**
     * Returns a week day {@code offset} of a <i>leap year</i>
     * from {@code 0} (corresponds to <i>Monday offset</i>) to
     * ({@link #DAYS_IN_WEEK} - 1) (corresponds to <i>Sunday offset</i>).
     * The first day of this <i>leap year</i> is <i>Monday</i>.
     * <p>
     * Getting the correct value of the day:
     * <p><pre>
     * int weekDayOffset = getWeekDayOffsetLeap(month, day);
     * int weekDay = weekDayOffset + getWeekDayInPeriodYear(year);
     * weekDay = ((weekDay - minWeekDay) % daysInWeek) + minWeekDay
     * </pre><p>
     * <b>Warning</b>: this method don't use verifications,
     * just returns a value in an array with provided indexes.
     * 
     * @param month from {@link Date#MIN_MONTH min month}
     *        to {@link Date#MAX_MONTH max month}.
     * @param day from {@link Date#MIN_DAY min day}
     *        to {@link Month#getDayAmount day amount}.
     * @return a day offset from {@code 0} to ({@link #DAYS_IN_WEEK} - 1).
     * @see {@link #getWeekDayInPeriodYear}
     */
    protected static int getWeekDayOffsetLeap(int month, int day) {
        return weekDayOffsetLeap[month][day];
    }

    /**
     * Returns a week day {@code offset} of a <i>not leap year</i>
     * from {@code 0} (corresponds to <i>Monday offset</i>) to
     * ({@link #DAYS_IN_WEEK} - 1) (corresponds to <i>Sunday offset</i>).
     * The first day of this <i>not leap year</i> is <i>Monday</i>.
     * <p>
     * Getting the correct value of the day:
     * <p><pre>
     * int weekDayOffset = getWeekDayOffsetNotLeap(month, day);
     * int weekDay = weekDayOffset + getWeekDayInPeriodYear(year);
     * weekDay = ((weekDay - minWeekDay) % daysInWeek) + minWeekDay
     * </pre><p>
     * <b>Warning</b>: this method don't use verifications,
     * just returns a value in an array with provided indexes.
     * 
     * @param month from {@link Date#MIN_MONTH min month}
     *        to {@link Date#MAX_MONTH max month}.
     * @param day from {@link Date#MIN_DAY min day}
     *        to {@link Month#getDayAmount day amount}.
     * @return a day offset from {@code 0} to ({@link #DAYS_IN_WEEK} - 1).
     * @see {@link #getWeekDayInPeriodYear}
     */
    protected static int getWeekDayOffsetNotLeap(int month, int day) {
        return weekDayOffsetNotLeap[month][day];
    }

//-Private-part-------------------------------------------------------------------------------------

    private static final int[] firstWeekDayInPeriodYear;
    private static final int[][] weekDayOffsetLeap;
    private static final int[][] weekDayOffsetNotLeap;

    static {
        firstWeekDayInPeriodYear = new int[LEAP_PERIOD + 1];
        weekDayOffsetLeap = new int[MONTHS_IN_YEAR + 1][MAX_DAY + 1];
        weekDayOffsetNotLeap = new int[MONTHS_IN_YEAR + 1][MAX_DAY + 1];

        // weekDayInPeriodYear

        int weekDay = MIN_VALUE; // Monday
        firstWeekDayInPeriodYear[1] = weekDay++;

        for (int y = 2; y <= LEAP_PERIOD; ++y) {
            if (isLeap(y - 1)) {
                firstWeekDayInPeriodYear[y] = ++weekDay;
                ++weekDay;
            } else {
                firstWeekDayInPeriodYear[y] = weekDay++;
            }
            weekDay = ((weekDay - MIN_VALUE) % DAYS_IN_WEEK) + MIN_VALUE;
        }
        firstWeekDayInPeriodYear[0] = firstWeekDayInPeriodYear[LEAP_PERIOD];

        // weekDayOffsetLeap

        int weekDayOffset = 0; // Monday offset
        for (int m = MIN_MONTH; m <= MAX_MONTH; ++m) {
            int dayAmount = Month.getDayAmountLeap(m);
            for (int d = MIN_DAY; d <= dayAmount; ++d) {
                weekDayOffsetLeap[m][d] = weekDayOffset;
                weekDayOffset = (weekDayOffset + 1) % DAYS_IN_WEEK;
            }
        }

        // weekDayOffsetNotLeap

        weekDayOffset = 0; // Monday offset
        for (int m = MIN_MONTH; m <= MAX_MONTH; ++m) {
            int dayAmount = Month.getDayAmountNotLeap(m);
            for (int d = MIN_DAY; d <= dayAmount; ++d) {
                weekDayOffsetNotLeap[m][d] = weekDayOffset;
                weekDayOffset = (weekDayOffset + 1) % DAYS_IN_WEEK;
            }
        }
    }
}
