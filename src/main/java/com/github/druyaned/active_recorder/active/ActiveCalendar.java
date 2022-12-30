package com.github.druyaned.active_recorder.active;

import com.github.druyaned.active_recorder.time.Date;
import com.github.druyaned.active_recorder.time.DateTime;

public class ActiveCalendar {
    public static final int YEAR_DIFFERENCE_LIMIT = (1 << 8);

    private final ActiveYear[] activeYears;
    private final int[] yearsByIndex;
    private int size;
    private final int minYear;
    private final Activities activities;

    /**
     * Creates with 1 active time which {@code start = (currentTime - 1 sec)},
     * {@code stop = currentTime}, {@code mode = ActiveMode.STAGNATION} and {@code descr = ""}.
     * 
     * @see ActiveMode#STAGNATION
     */
    public ActiveCalendar() {
        DateTime stop = DateTime.now();
        DateTime start = DateTime.of(stop.rawSeconds - 1);
        String descr = "Open ActiveRecorder";
        ActiveTime activeTime = new ActiveTime(start, stop, ActiveMode.STAGNATION, descr);

        minYear = start.getYear();
        int stopYear = stop.getYear();
        if (stopYear - minYear >= YEAR_DIFFERENCE_LIMIT) {
            String f = "%d[stopYear] - %d[startYear] >= %d[yearDifferenceLimit]";
            String m = String.format(f, stopYear, minYear, YEAR_DIFFERENCE_LIMIT);
            throw new IllegalArgumentException(m);
        }

        activeYears = new ActiveYear[YEAR_DIFFERENCE_LIMIT];
        yearsByIndex = new int[YEAR_DIFFERENCE_LIMIT];
        size = 0;
        activities = new Activities(activeTime);

        for (int i = 0; i < activities.size(); ++i) {
            Activity a = activities.get(i);
            int y = a.start.getYear();

            if (activeYears[y % YEAR_DIFFERENCE_LIMIT] == null) {
                activeYears[y % YEAR_DIFFERENCE_LIMIT] = new ActiveYear(a);
                yearsByIndex[size++] = y;
            } else {
                activeYears[y % YEAR_DIFFERENCE_LIMIT].updateBy(a);
            }
        }
    }

    public ActiveCalendar(ActiveTime activeTime) {
        minYear = activeTime.start.getYear();
        int stopYear = activeTime.stop.getYear();
        if (stopYear - minYear >= YEAR_DIFFERENCE_LIMIT) {
            String f = "%d[stopYear] - %d[startYear] >= %d[yearDifferenceLimit]";
            String m = String.format(f, stopYear, minYear, YEAR_DIFFERENCE_LIMIT);
            throw new IllegalArgumentException(m);
        }

        activeYears = new ActiveYear[YEAR_DIFFERENCE_LIMIT];
        yearsByIndex = new int[YEAR_DIFFERENCE_LIMIT];
        size = 0;
        activities = new Activities(activeTime);

        for (int i = 0; i < activities.size(); ++i) {
            Activity a = activities.get(i);
            int y = a.start.getYear();

            if (activeYears[y % YEAR_DIFFERENCE_LIMIT] == null) {
                activeYears[y % YEAR_DIFFERENCE_LIMIT] = new ActiveYear(a);
                yearsByIndex[size++] = y;
            } else {
                activeYears[y % YEAR_DIFFERENCE_LIMIT].updateBy(a);
            }
        }
    }

    public ActiveCalendar(ActiveTime[] activeTimes) {
        this(activeTimes[0]);
        for (int i = 1; i < activeTimes.length; ++i) {
            if (!add(activeTimes[i])) {
                throw new IllegalArgumentException("invalid activeTime: " + activeTimes[i]);
            }
        }
    }

    /**
     * Adds an activity or activities by the provieded {@code activeTime}.
     * New activities to add can be created when {@code start.date != start.date}.
     * 
     * @param activeTime to create activity or activities.
     * @return {@code true} if it was successfully or {@code null} if activeTime is invalid.
     * @throws ActivitiesSizeException if the {@link Activities#MAX_SIZE} is reached.
     * @see Activities
     */
    public synchronized boolean add(ActiveTime activeTime) throws ActivitiesSizeException {
        if (activeTime == null) {
            return false;
        }
        int y1 = activeTime.start.getYear(), y2 = activeTime.stop.getYear();
        if (y1 < minYear || y2 - minYear >= YEAR_DIFFERENCE_LIMIT) {
            throw new IllegalArgumentException("invalid activeTime: " + activeTime);
        }

        Activity a = activities.addWithSameDates(activeTime);
        if (a != null) {
            int y = a.start.getYear();

            if (activeYears[y % YEAR_DIFFERENCE_LIMIT] == null) {
                activeYears[y % YEAR_DIFFERENCE_LIMIT] = new ActiveYear(a);
                yearsByIndex[size++] = y;
            } else {
                activeYears[y % YEAR_DIFFERENCE_LIMIT].updateBy(a);
            }
            return true;
        }

        // if (a == null)
        Activity[] splitted = activities.addWithDifferentDates(activeTime);
        if (splitted == null) {
            return false;
        }
        for (int i = 0; i < splitted.length; ++i) {
            a = splitted[i];
            int y = a.start.getYear();

            if (activeYears[y % YEAR_DIFFERENCE_LIMIT] == null) {
                activeYears[y % YEAR_DIFFERENCE_LIMIT] = new ActiveYear(a);
                yearsByIndex[size++] = y;
            } else {
                activeYears[y % YEAR_DIFFERENCE_LIMIT].updateBy(a);
            }
        }

        return true;
    }

    public ActiveYear getActiveYear(int yearNumber) {
        if (yearNumber < minYear || yearNumber - minYear >= YEAR_DIFFERENCE_LIMIT) {
            throw new IndexOutOfBoundsException("invalid yearNumber " + yearNumber);
        }
        return activeYears[yearNumber % YEAR_DIFFERENCE_LIMIT];
    }

    public ActiveYear getFirstActiveYear() {
        return activeYears[minYear % YEAR_DIFFERENCE_LIMIT];
    }

    public ActiveYear getLastActiveYear() {
        return activeYears[yearsByIndex[size - 1] % YEAR_DIFFERENCE_LIMIT];
    }

    public ActiveYear getActiveYearBy(int index) {
        return activeYears[yearsByIndex[index] % YEAR_DIFFERENCE_LIMIT];
    }

    public int getActiveYearsSize() {
        return size;
    }

    public Activity getActivity(int index) {
        return activities.get(index);
    }

    public Activity getFirstActivity() {
        return activities.getFirst();
    }

    public Activity getLastActivity() {
        return activities.getLast();
    }

    public int getActivitiesSize() {
        return activities.size();
    }

    public Date getLastDate() {
        return activities.getLast().getStart().getDate();
    }
}
