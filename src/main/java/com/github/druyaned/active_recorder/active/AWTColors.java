package com.github.druyaned.active_recorder.active;

import static com.github.druyaned.active_recorder.active.ActiveColor.*;

import java.awt.Color;

public class AWTColors {
    private static final Color[] colors = new Color[AMOUNT];

    static {
        for (int i = 0, value = MIN_VALUE; i < AMOUNT; ++i, ++value) {
            ActiveColor ac = ActiveColor.getBy(value);
            colors[i] = new Color(ac.red, ac.green, ac.blue);
        }
    }

    public static Color getBy(ActiveColor ac) {
        return colors[ac.value - MIN_VALUE];
    }
}
