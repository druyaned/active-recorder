package com.github.druyaned.active_recorder.active;

import static com.github.druyaned.active_recorder.active.Activities.ZONE_ID;
import java.time.Instant;
import java.util.Arrays;

/**
 * TODO: fix java-docs
 * An array of {@link Activity activities} that meets the <i>requirement</i>:
 * <p><ol>
 *     <li>for {@link ActiveTime};</li>
 *     <li>for {@link Activity};</li>
 *     <li><code>activities[i-1].stop <= activities[i].start</code>.</li>
 * </ol><p>
 */
public final class ZonedActivities {
    private volatile int capacity;
    private volatile int size;
    private ZonedActivity[] activities;
    
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
    public ZonedActivities(Activity firstActivity) {
        capacity = 16;
        size = 0;
        activities = new ZonedActivity[capacity];
        ZonedActivity[] splitted = ZonedActivity.of(firstActivity, ZONE_ID);
        for (int i = 0; i < splitted.length; ++i) {
            if (size == capacity) {
                capacity <<= 1;
                activities = Arrays.copyOf(activities, capacity);
            }
            activities[size++] = splitted[i];
        }
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
    public ZonedActivities(Activity firstActivity, int initCapacity) {
        int c = 16;
        while (c < initCapacity) {
            c <<= 1;
        }
        capacity = c;
        size = 0;
        activities = new ZonedActivity[capacity];
        ZonedActivity[] splitted = ZonedActivity.of(firstActivity, ZONE_ID);
        for (int i = 0; i < splitted.length; ++i) {
            if (size == capacity) {
                capacity <<= 1;
                activities = Arrays.copyOf(activities, capacity);
            }
            activities[size++] = splitted[i];
        }
    }
    
//-Getters------------------------------------------------------------------------------------------
    
    public ZonedActivity get(int index) { return activities[index]; }
    
    public ZonedActivity getFirst() { return activities[0]; }
    
    public ZonedActivity getLast() { return activities[size - 1]; }
    
    public int capacity() { return capacity; }
    
    public int size() { return size; }
    
//-Methods------------------------------------------------------------------------------------------
    
    /**
     * TODO: write java-docs
     * @param a
     * @return 
     */
    public ZonedActivity[] add(Activity a) {
        Instant prevStop = activities[size - 1].getStop();
        Instant nextStart = a.getStart();
        if (prevStop.compareTo(nextStart) > 0) {
            String m = "prevStop > nextStart: " + prevStop + " > " + nextStart;
            throw new IllegalArgumentException(m);
        }
        ZonedActivity[] splitted = ZonedActivity.of(a, ZONE_ID);
        for (int i = 0; i < splitted.length; ++i) {
            if (size == capacity) {
                capacity <<= 1;
                activities = Arrays.copyOf(activities, capacity);
            }
            activities[size++] = splitted[i];
        }
        return splitted;
    }
}
