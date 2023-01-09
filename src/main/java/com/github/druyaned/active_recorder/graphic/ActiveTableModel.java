package com.github.druyaned.active_recorder.graphic;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

import javax.swing.table.*;

import com.github.druyaned.active_recorder.active.*;

public final class ActiveTableModel extends DefaultTableModel {
    private List<Color> colors = new ArrayList<>();

    public ActiveTableModel(Object[][] data,  String[] columnNames, ActiveDay activeDay) {
        super(data, columnNames);
        int activitiesSize = data.length;
        for (int r = 0; r < activitiesSize; ++r) {
            Activity a = activeDay.getZonedActivity(r);
            setRowColor(getRowCount() - 1, a.getMode());
        }
    }

    public void setRowColor(int row, ActiveMode mode) {
        colors.add(AWTColors.getBy(ActiveColor.getBy(mode)));
    }

    public Color getRowColor(int row) { return colors.get(row); }
}