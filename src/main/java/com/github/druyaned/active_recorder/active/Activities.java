package com.github.druyaned.active_recorder.active;

import static com.github.druyaned.active_recorder.time.DateTime.*;

import java.util.Arrays;

import com.github.druyaned.active_recorder.time.Date;
import com.github.druyaned.active_recorder.time.DateTime;

/**
 * An array of {@link Activity activities} that meets the <i>requirement</i>:
*  <p><ol>
*      <li>for {@link ActiveTime};</li>
*      <li>for {@link Activity};</li>
*      <li><code>activities[i-1].stop <= activities[i].start</code>.</li>
*  </ol><p>
 */
public final class Activities {
    public static final int MAX_SIZE = (1 << 21);

    private volatile int capacity;
    private volatile int size;
    private Activity[] activities;

    /**
     * Creates activities which meet the <i>requirements</i>:
     *  <p><ol>
     *      <li>for {@link ActiveTime}</li>
     *      <li>for {@link Activity}</li>
     *      <li><code>activities[i-1].stop <= activities[i].start</code></li>
     *  </ol><p>
     * 
     * @param activeTime to create an activity or activities.
     */
    public Activities(ActiveTime activeTime) {
        this(16, activeTime);
    }

    /**
     * Creates activities with the initial capacity which meet the <i>requirements</i>:
     *  <p><ol>
     *      <li>for {@link ActiveTime}</li>
     *      <li>for {@link Activity}</li>
     *      <li><code>activities[i-1].stop <= activities[i].start</code></li>
     *  </ol><p>
     * 
     * @param initCapacity initial capacity of the array of the activities.
     * @param firstActiveTime to create an activity or activities.
     */
    public Activities(int initCapacity, ActiveTime firstActiveTime) {
        if (initCapacity < 1 || initCapacity > MAX_SIZE) {
            throw new IllegalArgumentException("invalid initialCapacity " + initCapacity);
        }

        int c = 16;
        while (initCapacity > c) {
            c <<= 1;
        }
        capacity = c;
        size = 0;
        activities = new Activity[capacity];

        if (Activity.meetsRequirements(firstActiveTime)) {
            activities[size] = new Activity(firstActiveTime, size++);
        } else {
            Activity[] splitted = getSplittedWithAddition(firstActiveTime);
            if (splitted == null) {
                throw new IllegalArgumentException("invalid first active time " + firstActiveTime);
            }
        }
    }

    /**
     * Adds the activity by the provided {@code activeTime} which meets the <i>requirements</i>:
     *  <p><ol>
     *      <li>for {@link ActiveTime}</li>
     *      <li>for {@link Activity}</li>
     *      <li><code>activities[i-1].stop <= activities[i].start</code></li>
     *  </ol><p>
     * Furthermore returns a new activity by this activeTime.
     * <p>
     * The method should be used with {@link #addWithDifferentDates}.
     * 
     * @param activeTime to make a new activity.
     * @return {@code null} if the {@code activeTime} doesn't meet the <i>requirements</i>,
     *         <p>otherwise - a new added activity.
     * @throws ActivitiesSizeException if the {@link #MAX_SIZE} is reached.
     * @see #addWithDifferentDates
     */
    Activity addWithSameDates(ActiveTime activeTime) throws ActivitiesSizeException {
        if (size == MAX_SIZE) {
            throw new ActivitiesSizeException(MAX_SIZE);
        }
        if (!Activity.meetsRequirements(activeTime)) {
            return null;
        }
        if (activities[size - 1].stop.compareTo(activeTime.start) > 0) {
            return null;
        }

        return addAn(new Activity(activeTime, size));
    }

    /**
     * Splits the {@code activeTime} which has a <i>peculiarity</i>
     * {@code a.start.date != a.stop.date} to meet the <i>requirements</i>:
     *  <p><ol>
     *      <li>for {@link ActiveTime}</li>
     *      <li>for {@link Activity}</li>
     *      <li><code>activities[i-1].stop <= activities[i].start</code></li>
     *  </ol><p>
     * Furthermore adds new splitted activities and returns them.
     * <p>
     * The method should be used with {@link #addWithSameDates}.
     * 
     * @param activeTime to make new splitted activities.
     * @return {@code null} if the {@code activeTime} doesn't have the <i>peculiarity</i>,
     *         <p>{@code null} if {@code activities[size - 1].stop > activeTime.start},
     *         <p>otherwise - activities that meet with the <i>requirements</i>.
     * @throws ActivitiesSizeException if the {@link #MAX_SIZE} is reached.
     * @see #addWithSameDates
     */
    Activity[] addWithDifferentDates(ActiveTime activeTime) throws ActivitiesSizeException {
        if (size >= MAX_SIZE) {
            throw new ActivitiesSizeException(MAX_SIZE);
        }
        if (Activity.meetsRequirements(activeTime)) {
            return null;
        }
        if (activities[size - 1].stop.compareTo(activeTime.start) > 0) {
            return null;
        }

        return getSplittedWithAddition(activeTime);
    }

    /**
     * Adds a new activity by the provided active time which should meet the <i>requirements</i>
     * and returns added {@code activity} or {@code activities}.
     * <p>
     * The <i>requirements</i> are:
     *  <p><ol>
     *      <li>for {@link ActiveTime}</li>
     *      <li>for {@link Activity}</li>
     *      <li><code>activities[i-1].stop <= activities[i].start</code></li>
     *  </ol><p>
     * 
     * @param activeTime to create active
     * @return either {@code new activities} if it lasted more than 1 day or a new {@code activity}.
     * @throws ActivitiesSizeException if the {@link #MAX_SIZE} is reached.
    */
    public synchronized Object add(ActiveTime activeTime) throws ActivitiesSizeException {
        if (size == MAX_SIZE) {
            throw new ActivitiesSizeException(MAX_SIZE);
        }
        if (activeTime == null) {
            return null;
        }
        if (activities[size - 1].stop.compareTo(activeTime.start) <= 0) {
            if (Activity.meetsRequirements(activeTime)) {
                return addAn(new Activity(activeTime, size));
            } else {
                return getSplittedWithAddition(activeTime);
            }
        }
        return null;
    }

    public Activity get(int index) {
        return activities[index];
    }

    public Activity getFirst() {
        return activities[0];
    }

    public Activity getLast() {
        return activities[size - 1];
    }

    public int capacity() {
        return capacity;
    }

    public int size() {
        return size;
    }

//-Private-methods----------------------------------------------------------------------------------

    // including an increase of the capacity but without a MAX_SIZE check
    private Activity addAn(Activity a) {
        if (size == capacity) {
            capacity <<= 1;
            activities = Arrays.copyOf(activities, capacity);
        }

        return activities[size++] = a;
    }

    // the activeTime must be splitted for sure; has a MAX_SIZE check
    private Activity[] getSplittedWithAddition(ActiveTime activeTime)
        throws ActivitiesSizeException
    {
        // designations
        DateTime t1 = activeTime.start;
        DateTime t2 = activeTime.stop;
        int t1DaySec = t1.getDaySeconds();
        int t2DaySec = t2.getDaySeconds();
        long secDuration = activeTime.getDuration();

        // splitting array size calculation
/*
+0: t1DaySec == MIN_SECOND && t2DaySec == MIN_SECOND
+2: t1DaySec > t2DaySec && t1 != MIN_SECOND && t2 != MIN_SECOND
+1: all others

s - day separator
00:00:00
    00:00:00 s1--------s---------s2--------s +0
    02:53:48 s1--------s-2-------s---------s +1
    12:00:00 s1--------s----2----s---------s +1
    17:53:42 s1--------s-------2-s---------s +1
    23:59:59 s1--------s--------2s---------s +1
02:53:48
    00:00:00 s-1-------s2--------s---------s +1
    02:53:48 s-1-------s-2-------s---------s +1
    12:00:00 s-1-------s----2----s---------s +1
    17:53:42 s-1-------s-------2-s---------s +1
    23:59:59 s-1-------s--------2s---------s +1
12:00:00
    00:00:00 s----1----s2--------s---------s +1
    02:53:48 s----1----s-2-------s---------s +2
    12:00:00 s----1----s----2----s---------s +1
    17:53:42 s----1----s-------2-s---------s +1
    23:59:59 s----1----s--------2s---------s +1
17:53:42
    00:00:00 s-------1-s2--------s---------s +1
    02:53:48 s-------1-s-2-------s---------s +2
    12:00:00 s-------1-s----2----s---------s +2
    17:53:42 s-------1-s-------2-s---------s +1
    23:59:59 s-------1-s--------2s---------s +1
23:59:59
    00:00:00 s--------1s2--------s---------s +1
    02:53:48 s--------1s-2-------s---------s +2
    12:00:00 s--------1s----2----s---------s +2
    17:53:42 s--------1s-------2-s---------s +2
    23:59:59 s--------1s--------2s---------s +2
*/
        int splittedSize;
        boolean t1DaySecIsMin = t1DaySec == MIN_SECOND;
        boolean t2DaySecIsMin = t2DaySec == MIN_SECOND;
        if (t1DaySecIsMin && t2DaySecIsMin) {
            splittedSize = (int) (secDuration / SECONDS_IN_DAY);
        } else if (t1DaySec > t2DaySec && !t1DaySecIsMin && !t2DaySecIsMin) {
            splittedSize = (int) (secDuration / SECONDS_IN_DAY) + 2;
        } else {
            splittedSize = (int) (secDuration / SECONDS_IN_DAY) + 1;
        }

        if (splittedSize > MAX_SIZE - size) { // MAX_SIZE check
            throw new ActivitiesSizeException(MAX_SIZE);
        }

        // splitting preparation
        Activity[] splitted = new Activity[splittedSize];
        int splitIndex = 0;
        ActiveMode mode = activeTime.getMode();
        String descr = activeTime.descr;
        int rawDays1 = t1.getDate().rawDays;
        int rawDays2 = t2.getDate().rawDays;

        // splitting the activity
        DateTime start = t1;
        DateTime stop = DateTime.of(Date.of(rawDays1 + 1), MIN_HOUR, MIN_MINUTE, MIN_SECOND);
        Activity a = new Activity(t1, stop, mode, descr, size);
        splitted[splitIndex++] = a;
        addAn(a);

        for (int r = rawDays1 + 1; r < rawDays2; ++r) {
            start = DateTime.of(Date.of(r), MIN_HOUR, MIN_MINUTE, MIN_SECOND);
            stop = DateTime.of(Date.of(r + 1), MIN_HOUR, MIN_MINUTE, MIN_SECOND);
            a = new Activity(start, stop, mode, descr, size);
            splitted[splitIndex++] = a;
            addAn(a);
        }

        if (!t2DaySecIsMin) {
            start = DateTime.of(Date.of(rawDays2), MIN_HOUR, MIN_MINUTE, MIN_SECOND);
            stop = t2;
            a = new Activity(start, stop, mode, descr, size);
            splitted[splitIndex++] = a;
            addAn(a);
        }

        return splitted;
    }
}
