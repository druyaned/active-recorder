package com.github.druyaned.active_recorder.active;

public class ActiveCalendar {
    public static int YEAR_DIFFERENCE_LIMIT = (1 << 8);
    private static final String WRONG_YEAR_FORMAT = "failed condition " +
            "{ y1 >= minYear && y2 - minYear < yearDifferenceLimit }" +
            ", where y1=%d, minYear=%d, y2=%d, yearDifferenceLimit=%d";
    
    private final ActiveYear[] activeYears;
    private final int[] yearsByIndex;
    private int size;
    private final int minYear;
    private final Activities activities;
    private final ZonedActivities zonedActivities;
    
    public ActiveCalendar(Activity[] activities) {
        this.activities = new Activities(activities[0]);
        zonedActivities = new ZonedActivities(activities[0]);
        for (int i = 1; i < activities.length; ++i) {
            this.activities.add(activities[i]);
            zonedActivities.add(activities[i]);
        }
        ZonedActivity zonedFirst = zonedActivities.getFirst();
        ZonedActivity zonedLast = zonedActivities.getLast();
        minYear = zonedFirst.getZonedStart().getYear();
        int stopYear = zonedLast.getZonedStop().getYear();
        if (stopYear - minYear >= YEAR_DIFFERENCE_LIMIT) {
            throw new IllegalArgumentException(String.format(
                    WRONG_YEAR_FORMAT, minYear, minYear, stopYear, YEAR_DIFFERENCE_LIMIT));
        }
        activeYears = new ActiveYear[YEAR_DIFFERENCE_LIMIT];
        yearsByIndex = new int[YEAR_DIFFERENCE_LIMIT];
        size = 0;
        for (int i = 0; i < zonedActivities.size(); ++i) {
            ZonedActivity zoned = zonedActivities.get(i);
            int y = zoned.getZonedStart().getYear();
            if (activeYears[y % YEAR_DIFFERENCE_LIMIT] == null) {
                activeYears[y % YEAR_DIFFERENCE_LIMIT] = new ActiveYear(zoned);
                yearsByIndex[size++] = y;
            } else {
                activeYears[y % YEAR_DIFFERENCE_LIMIT].updateBy(zoned);
            }
        }
    }

    /**
     * Adds an activity or activities by the provided {@code activeTime}.
     * New activities to add can be created when {@code start.date != start.date}.
     * 
     * @param a to create activity or activities.
     * @see Activities
     */
    public final synchronized void add(Activity a) {
        ZonedActivity[] addedZoned = zonedActivities.add(a);
        activities.add(a);
        ZonedActivity firstAdded = addedZoned[0];
        ZonedActivity lastAdded = addedZoned[addedZoned.length - 1];
        int y1 = firstAdded.getZonedStart().getYear(), y2 = lastAdded.getZonedStop().getYear();
        if (y1 < minYear || y2 - minYear >= YEAR_DIFFERENCE_LIMIT) {
            throw new IllegalArgumentException(String.format(
                    WRONG_YEAR_FORMAT, y1, minYear, y2, YEAR_DIFFERENCE_LIMIT));
        }
        for (int i = 0; i < addedZoned.length; ++i) {
            int y = addedZoned[i].getZonedStart().getYear();
            if (activeYears[y % YEAR_DIFFERENCE_LIMIT] == null) {
                activeYears[y % YEAR_DIFFERENCE_LIMIT] = new ActiveYear(addedZoned[i]);
                yearsByIndex[size++] = y;
            } else {
                activeYears[y % YEAR_DIFFERENCE_LIMIT].updateBy(addedZoned[i]);
            }
        }
    }

    public ActiveYear getActiveYear(int yearNumber) {
        if (yearNumber < minYear || yearNumber - minYear >= YEAR_DIFFERENCE_LIMIT) {
            throw new IndexOutOfBoundsException(String.format(
                    WRONG_YEAR_FORMAT, yearNumber, minYear, yearNumber, YEAR_DIFFERENCE_LIMIT));
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

    public int getActiveYearsSize() { return size; }

    public ZonedActivity getZonedActivity(int index) { return zonedActivities.get(index); }

    public ZonedActivity getFirstZonedActivity() { return zonedActivities.getFirst(); }

    public ZonedActivity getLastZonedActivity() { return zonedActivities.getLast(); }

    public int getZonedActivitiesSize() { return zonedActivities.size(); }
    
    public Activity getActivity(int index) { return activities.get(index); }

    public Activity getFirstActivity() { return activities.getFirst(); }

    public Activity getLastActivity() { return activities.getLast(); }

    public int getActivitiesSize() { return activities.size(); }
}
