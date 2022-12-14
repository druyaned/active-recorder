package com.github.druyaned.active_recorder.active;

import java.time.Instant;
import java.util.Arrays;
import java.time.ZoneId;

/**
 * TODO: fix java-docs
 * An array of {@link Activity activities} that meets the <i>requirement</i>:
 * <p><ol>
 *     <li>for {@link ActiveTime};</li>
 *     <li>for {@link Activity};</li>
 *     <li><code>activities[i-1].stop <= activities[i].start</code>.</li>
 * </ol><p>
 */
public final class Activities {
    public static final ZoneId ZONE_ID = ZoneId.systemDefault();
    
    private volatile int capacity;
    private volatile int size;
    private Activity[] activities;
    
    /**
     * TODO: fix java-docs
     * Creates activities with the initial capacity which meet the <i>requirements</i>:
     * <p><ol>
     *     <li>for {@link ActiveTime}</li>
     *     <li>for {@link Activity}</li>
     *     <li><code>activities[i-1].stop <= activities[i].start</code></li>
     * </ol><p>
     * 
     * @param firstActivity to create an activity or activities.
     */
    public Activities(Activity firstActivity) {
        capacity = 16;
        size = 0;
        activities = new Activity[capacity];
        activities[size++] = firstActivity;
    }
    
    /**
     * TODO: fix java-docs
     * Creates activities with the initial capacity which meet the <i>requirements</i>:
     * <p><ol>
     *     <li>for {@link ActiveTime}</li>
     *     <li>for {@link Activity}</li>
     *     <li><code>activities[i-1].stop <= activities[i].start</code></li>
     * </ol><p>
     * 
     * @param firstActivity to create an activity or activities.
     * @param initCapacity
     */
    public Activities(Activity firstActivity, int initCapacity) {
        int c = 16;
        while (c < initCapacity) {
            c <<= 1;
        }
        capacity = c;
        size = 0;
        activities = new Activity[capacity];
        activities[size++] = firstActivity;
    }
    
//-Getters------------------------------------------------------------------------------------------
    
    public Activity get(int index) { return activities[index]; }
    
    public Activity getFirst() { return activities[0]; }
    
    public Activity getLast() { return activities[size - 1]; }
    
    public int capacity() { return capacity; }
    
    public int size() { return size; }
    
//-Methods------------------------------------------------------------------------------------------
    
    /**
     * TODO: write java-docs
     * @param a
     * @return 
     */
    public Activity add(Activity a) {
        Instant prevStop = activities[size - 1].getStop();
        Instant nextStart = a.getStart();
        if (prevStop.compareTo(nextStart) > 0) {
            String m = "prevStop > nextStart: " + prevStop + " > " + nextStart;
            throw new IllegalArgumentException(m);
        }
        if (size == capacity) {
            capacity <<= 1;
            activities = Arrays.copyOf(activities, capacity);
        }
        return activities[size++] = a;
    }
}
