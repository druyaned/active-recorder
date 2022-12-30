package com.github.druyaned.active_recorder.active;

import static com.github.druyaned.active_recorder.active.ActiveMode.*;

/**
 * Provides a color of an activity to evaluate. The class is <i>immutable</i>.
*/
public class ActiveColor implements Comparable<ActiveColor> {
    public static final int MIN_COLOR = 63;
    public static final int MAX_COLOR = 255;

    public static final int MIN_VALUE = MIN_COLOR - MAX_COLOR; // -192
    public static final int MAX_VALUE = -MIN_VALUE; // 192
    public static final int AMOUNT = MAX_VALUE - MIN_VALUE + 1; // 385

    public static final ActiveColor DEVELOPMENT_COLOR;
    public static final ActiveColor STAGNATION_COLOR;
    public static final ActiveColor RELAXATION_COLOR;

    private static final ActiveColor[] colors = new ActiveColor[AMOUNT];

    static {
        for (int i = 0, value = MIN_VALUE; i < AMOUNT; ++i, ++value) {
            colors[i] = new ActiveColor(value);
        }

        DEVELOPMENT_COLOR = colors[AMOUNT - 1]; // ind=384, greenest
        STAGNATION_COLOR = colors[AMOUNT / 2]; // ind=192, white
        RELAXATION_COLOR = colors[0]; // reddest
    }

    public static ActiveColor getBy(int value) {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IndexOutOfBoundsException("invalid value " + value);
        }

        return colors[value - MIN_VALUE];
    }

    public static ActiveColor getBy(ActiveMode mode) {
        if (mode == DEVELOPMENT) { // the greenest
            return colors[AMOUNT - 1]; // ind=384
        }
        if (mode == STAGNATION) { // white
            return colors[AMOUNT / 2]; // ind=192
        }
        if (mode == RELAXATION) {
            return colors[0]; // the reddest
        }
        return null;
    }

//-Non-static---------------------------------------------------------------------------------------

    public final int red, green, blue;
    
    /**
     * A greener color is bigger than a redder.
     * <i>Max green</i> corresponds {@link #MAX_VALUE} which is positive.
     * <i>Max red</i> corresponds {@link #MIN_VALUE} which is negative.
     * <i>White</i> corresponds {@code 0}.
<pre>
___|___R____G____B___|___________
max|   63  255   63  |development
mid|  255  255  255  |stagnation
min|  255   63   63  |relaxation
</pre>
     * @return the value of this color from {@link #MIN_VALUE min value}
     *         to {@link #MAX_VALUE max value}.
     */
    public final int value;

    private ActiveColor(int value) {
        if (value < MIN_VALUE || value > MAX_VALUE)
            throw new IllegalArgumentException("invalid value " + value);

        final int MXC = MAX_COLOR;
        if (value < 0) {
            red = MXC;
            green = MXC + value;
            blue = MXC + value;
        }
        else {
            red = MXC - value;
            green = MXC;
            blue = MXC - value;
        }

        this.value = value;
    }

    public int getRed() { return red; }
    public int getGreen() { return green; }
    public int getBlue() { return blue; }
    public int getValue() { return value; }

    /**
     * A greener color is bigger than a redder.
     * 
     * @param o other {@link ActiveColor activity color}.
     * @return difference between {@link #getValue() values}:
     *         {@code positive} if the instance is greener
     *         than the {@code other}, {@code 0} if they are equal
     *         and {@code negative} if the instance is redder.
     */
    @Override
    public int compareTo(ActiveColor o) {
        return value - o.value;
    }

    @Override
    public boolean equals(Object other) {
        ActiveColor o = (ActiveColor) other;
        return value == o.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return "[red=" + red + ", green=" + green + ", blue=" + blue +
            ", value=" + value + "]";
    }
}
