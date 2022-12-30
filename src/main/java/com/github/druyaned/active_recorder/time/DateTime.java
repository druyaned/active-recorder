package com.github.druyaned.active_recorder.time;

public final class DateTime implements Comparable<DateTime> {

    public static final int MIN_HOUR = 0;
    public static final int MAX_HOUR = 23;
    public static final int MIN_MINUTE = 0;
    public static final int MAX_MINUTE = 59;
    public static final int MIN_SECOND = 0;
    public static final int MAX_SECOND = 59;

    public static final int HOURS_IN_DAY = 24;
    public static final int MINUTES_IN_HOUR = 60;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int MILLIS_IN_SECOND = 1000;
    
    public static final int SECONDS_IN_DAY = HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE;

    public static final long MIN_RAW_SECONDS = 0;
    public static final long MAX_RAW_SECONDS = (long) Date.MAX_RAW_DAYS * SECONDS_IN_DAY +
        MAX_HOUR * MINUTES_IN_HOUR * SECONDS_IN_MINUTE + MAX_MINUTE * SECONDS_IN_MINUTE +
        MAX_SECOND;

    public static DateTime now() {
        long rawSeconds = System.currentTimeMillis() / MILLIS_IN_SECOND;
        return of(rawSeconds);
    }

    public static DateTime of(long rawSeconds) {
        if (rawSeconds < MIN_RAW_SECONDS || rawSeconds > MAX_RAW_SECONDS)
            throw new IllegalArgumentException("invalid rawSeconds " + rawSeconds);

        // Date
        int rawDays = (int) (rawSeconds / SECONDS_IN_DAY);
        Date date = Date.of(rawDays);

        // Hours, Minutes, Seconds
        int dayAsSeconds = (int) (rawSeconds % SECONDS_IN_DAY);
        int dayAsMinutes = dayAsSeconds / SECONDS_IN_MINUTE;
        int dayAsHours = dayAsMinutes / MINUTES_IN_HOUR;
        int h = dayAsHours;
        int m = dayAsMinutes % MINUTES_IN_HOUR;
        int s = dayAsSeconds % SECONDS_IN_MINUTE;

        return new DateTime(rawSeconds, date, h, m, s);
    }

    public static DateTime of(int year, int month, int day, int h, int m, int s) {
        if (h < MIN_HOUR || h > MAX_HOUR)
            throw new IllegalArgumentException("invalid hour " + h);
        if (m < MIN_MINUTE || m > MAX_MINUTE)
            throw new IllegalArgumentException("invalid minute " + m);
        if (s < MIN_SECOND || s > MAX_SECOND)
            throw new IllegalArgumentException("invalid second " + s);

        Date date = Date.of(year, month, day);
        long rawSeconds = (long) date.rawDays * SECONDS_IN_DAY +
            SECONDS_IN_MINUTE * MINUTES_IN_HOUR * h + SECONDS_IN_MINUTE * m + s;

        return new DateTime(rawSeconds, date, h, m, s);
    }

    public static DateTime of(Date date, int h, int m, int s) {
        if (h < MIN_HOUR || h > MAX_HOUR)
            throw new IllegalArgumentException("invalid hour " + h);
        if (m < MIN_MINUTE || m > MAX_MINUTE)
            throw new IllegalArgumentException("invalid minute " + m);
        if (s < MIN_SECOND || s > MAX_SECOND)
            throw new IllegalArgumentException("invalid second " + s);

        long rawSeconds = (long) date.rawDays * SECONDS_IN_DAY +
            SECONDS_IN_MINUTE * MINUTES_IN_HOUR * h + SECONDS_IN_MINUTE * m + s;

        return new DateTime(rawSeconds, date, h, m, s);
    }

//-Non-static-part----------------------------------------------------------------------------------

    /**
     * "<i>Raw seconds</i>" means the amount of seconds
     * counting from 00:00 of the {@link Date#BASE_DATE base date}.
     */
    public final long rawSeconds;

    private final Date date;
    public final int hour;
    public final int minute;
    public final int second;

    private DateTime(long rawSeconds, Date date, int h, int m, int s) {
        this.rawSeconds = rawSeconds;

        this.date = date;
        this.hour = h;
        this.minute = m;
        this.second = s;
    }

    /**
     * "<i>Raw seconds</i>" means the amount of {@code seconds}
     * counting from 00:00 of the {@link Date#BASE_DATE base date}.
     * 
     * @return the amount of {@code seconds} counting from 00:00 of the
     *         {@link Date#BASE_DATE base date}.
     */
    public long getRawSeconds() {
        return rawSeconds;
    }

    /**
     * Returns day time seconds; e.g. if a day time is 01:01:01, returning value is 3661.
     *
     * @return day time as seconds.
     */
    public int getDaySeconds() {
        return hour * MINUTES_IN_HOUR * SECONDS_IN_MINUTE + minute * SECONDS_IN_MINUTE + second;
    }

    public Date getDate() { return date; }
    public int getYear() { return date.year; }
    public int getMonth() { return date.month; }
    public int getDay() { return date.day; }
    public int getHour() { return hour; }
    public int getMinute() { return minute; }
    public int getSecond() { return second; }

    @Override
    public int compareTo(DateTime o) {
        return Long.compare(rawSeconds, o.rawSeconds);
    }

    @Override
    public boolean equals(Object other) {
        DateTime o = (DateTime) other;
        return date.equals(o.date) && rawSeconds == o.rawSeconds;
    }

    @Override
    public String toString() {
        String f = "%d-%02d-%02d_%02d:%02d:%02d";
        return String.format(f, date.year, date.month, date.day, hour, minute, second);
    }
}
