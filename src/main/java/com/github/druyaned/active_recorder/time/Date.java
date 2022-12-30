package com.github.druyaned.active_recorder.time;

public class Date implements Comparable<Date> {

    /** Every leap period <i>positions</i> of leap years are the same. */
    public static final int LEAP_PERIOD = 400;

    /**
     * The counting of the system time begins from this year beginning.
     * <i>UTC 1970/1/1 00:00:00</i>.
     */
    public static final int BASE_YEAR = 1970;

    public static final int MIN_YEAR = BASE_YEAR;
    public static final int MAX_YEAR = (1 << 16); // 65536
    public static final int MIN_DAY = 1;
    public static final int MAX_DAY = 31;
    public static final int MIN_MONTH = 1;
    public static final int MAX_MONTH = 12;
    
    public static final long MIN_RAW_DAYS = 0;
    public static final long MAX_RAW_DAYS;

    public static final int MONTHS_IN_YEAR = 12;
    public static final int DAYS_IN_LEAP_YEAR = 366;
    public static final int DAYS_IN_NOTLEAP_YEAR = 365;
    public static final int DAYS_IN_WEEK = 7;

    public static final long MILLIS_IN_DAY = 1000 * 60 * 60 * 24;

    public static final Date BASE_DATE;
    public static final Date MAX_DATE;

    /**
     * Days have passed since the {@link #BASE_YEAR base year} till the end of the
     * <i>offset year</i>.
     * <p>
     * <i>Offset Year</i> - a year from the {@link #LEAP_PERIOD} where the
     * {@code first year} is {@code 0} and corresponds to {@link #BASE_YEAR}.
     * <pre>offsetYear = (year - BASE_YEAR) % LEAP_PERIOD</pre>
     */
    private static final int[] daysAfterOffsetYear;

    /** Days till the ended month of a leap year. */
    private static final int[] daysAfterMonthLeap = {
        0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366
    };

    /** Days till the ended month of a not leap year. */
    private static final int[] daysAfterMonthNotLeap = {
        0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365
    };

    static {
        daysAfterOffsetYear = new int[LEAP_PERIOD + 1];

        boolean baseLeapYeap = isLeap(BASE_YEAR);
        if (baseLeapYeap)
            daysAfterOffsetYear[0] = DAYS_IN_LEAP_YEAR;
        else
            daysAfterOffsetYear[0] = DAYS_IN_NOTLEAP_YEAR;

        for (int i = 1; i <= LEAP_PERIOD; ++i) {
            if (isLeap(i + BASE_YEAR))
                daysAfterOffsetYear[i] = DAYS_IN_LEAP_YEAR + daysAfterOffsetYear[i - 1];
            else
                daysAfterOffsetYear[i] = DAYS_IN_NOTLEAP_YEAR + daysAfterOffsetYear[i - 1];
        }

        if (baseLeapYeap)
            BASE_DATE = Date.of(BASE_YEAR, MAX_MONTH, Month.getDayAmountLeap(MAX_MONTH));
        else
            BASE_DATE = Date.of(BASE_YEAR, MAX_MONTH, Month.getDayAmountNotLeap(MAX_MONTH));

        if (isLeap(MAX_YEAR)) {
            MAX_DATE = Date.of(MAX_YEAR, MAX_MONTH, Month.getDayAmountLeap(MAX_MONTH));
            MAX_RAW_DAYS = MAX_DATE.rawDays;
        } else {
            MAX_DATE = Date.of(MAX_YEAR, MAX_MONTH, Month.getDayAmountNotLeap(MAX_MONTH));
            MAX_RAW_DAYS = MAX_DATE.rawDays;
        }
    }

    public static boolean isLeap(int year) {
        if (year % 400 == 0)
            return true;
        else if (year % 100 == 0)
            return false;
        else if (year % 4 == 0)
            return true;
        else
            return false;
    }

    public static Date now() {
        int rawDays = (int) (System.currentTimeMillis() / MILLIS_IN_DAY);
        return of(rawDays);
    }

    public static Date of(int rawDays) {
        if (rawDays < MIN_RAW_DAYS || rawDays > MAX_RAW_DAYS)
            throw new IllegalArgumentException("invalid rawDays " + rawDays);

        // Year
        int offsetDays = (int) (rawDays % daysAfterOffsetYear[LEAP_PERIOD - 1]);
        int offsetYear = binSearch(daysAfterOffsetYear, offsetDays);
        int addPeriods = (rawDays / daysAfterOffsetYear[LEAP_PERIOD - 1]);
        int year = offsetYear + addPeriods * LEAP_PERIOD + BASE_YEAR;

        // Month, Day
        int yearDays;
        if (offsetYear == 0)
            yearDays = offsetDays;
        else
            yearDays = offsetDays - daysAfterOffsetYear[offsetYear - 1];

        int month;
        int day;
        if (isLeap(year)) {
            month = binSearch(daysAfterMonthLeap, yearDays);
            day = yearDays - daysAfterMonthLeap[month - 1] + 1;
        } else {
            month = binSearch(daysAfterMonthNotLeap, yearDays);
            day = yearDays - daysAfterMonthNotLeap[month - 1] + 1;
        }

        return new Date(rawDays, year, month, day);
    }

    public static Date of(int year, int month, int day) {
        if (year < MIN_YEAR || year > MAX_YEAR) {
            throw new IllegalArgumentException("invalid year " + year);
        }
        if (month < MIN_MONTH || month > MAX_MONTH) {
            throw new IllegalArgumentException("invalid month " + month);
        }

        int days;
        boolean leapYear = isLeap(year);
        if (leapYear) {
            days = Month.getDayAmountLeap(month);
        } else {
            days = Month.getDayAmountNotLeap(month);
        }
        if (day < MIN_DAY || day > days) {
            throw new IllegalArgumentException("invalid day " + day);
        }

        int offsetYear = (year - BASE_YEAR) % LEAP_PERIOD;
        int addPeriods = (year - BASE_YEAR - offsetYear) / LEAP_PERIOD;
        int addDays = addPeriods * daysAfterOffsetYear[LEAP_PERIOD - 1];

        int offsetDays;
        if (leapYear) {
            offsetDays = (day - 1) + daysAfterMonthLeap[month - 1];
        } else {
            offsetDays = (day - 1) + daysAfterMonthNotLeap[month - 1];
        }
        if (offsetYear != 0) {
            offsetDays += daysAfterOffsetYear[offsetYear - 1];
        }

        int rawDays = offsetDays + addDays;
        return new Date(rawDays, year, month, day);
    }

    /**
     * Binary search for local sorted private arrays
     * {@link #daysAfterOffsetYear},
     * {@link #daysAfterMonthLeap} and {@link #daysAfterMonthNotLeap}.
     * 
     * <pre>
     *  if (lastElement < key) return arraySize;
     *  else if (a[index] > key) return index;
     *  else return (index + 1);
     * </pre>
     * 
     * @param a an array to search in.
     * @param key a value to find.
     * @return either {@code a.length} if the {@code key} is bigger
     *         than {@code a[a.length - 1]}
     *         or such an index: {@code a[index] > key}.
     */
    private static int binSearch(int a[], int key) {
        int low = 0;
        int high = a.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = a[mid];

            if (midVal < key) {
                low = mid + 1;
            } else if (midVal > key) {
                high = mid - 1;
            } else {
                return mid + 1; // key found
            }
        }
        return low; // key not found.
    }

//------------------------------------------------------------------------------
    // Non-static part

    /**
     * "<i>Raw days</i>" means the amount of days
     * counting from the {@link #BASE_DATE base date}.
     */
    public final int rawDays;

    public final int year;
    public final int month;
    public final int day;

    private Date(int rawDays, int y, int m, int d) {
        this.rawDays = rawDays;

        this.year = y;
        this.month = m;
        this.day = d;
    }

    @Override
    public int compareTo(Date o) {
        return rawDays - o.rawDays;
    }

    @Override
    public boolean equals(Object other) {
        Date o = (Date) other;
        return rawDays == o.rawDays;
    }

    @Override
    public int hashCode() {
        return rawDays;
    }

    @Override
    public String toString() {
        String f = "%d-%02d-%02d";
        return String.format(f, year, month, day);
    }
}
