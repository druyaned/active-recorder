package com.github.druyaned.active_recorder.graphic;

import com.github.druyaned.active_recorder.active.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import javax.swing.*;

public class DayFrame extends JFrame {
    public static final int W = 512;
    public static final int H = 448;
    public static final int X = AppFrame.X + AppFrame.W;
    public static final int Y = AppFrame.Y;
    private static final String TIME_FORMAT = "%02d:%02d:%02d";

    private final JPanel contentPane;
    private JTable activitiesTable;
    private LocalDate currentDate;
    private int currentActivitiesSize;
    private ActiveTableModel tableModel;
    private JScrollPane scrollPane;

    public DayFrame(ActiveCalendar calendar) {
        setLocation(X, Y);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setPreferredSize(new Dimension(W, H));

        // setting up the content pane
        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        // add close actions
        CloseActionsAdder.add(contentPane, () -> {
            if (isVisible()) {
                setVisible(false);
            }
        });

        LocalDate lastDate = calendar.getLastZonedActivity().getZonedStart().toLocalDate();
        ActiveDay lastActiveDay = calendar.getActiveYear(lastDate.getYear())
                .getActiveMonth(lastDate.getMonthValue()).getActiveDay(lastDate.getDayOfMonth());
        currentDate = lastDate;
        currentActivitiesSize = lastActiveDay.getZonedActivitiesSize();
        remakeBy(lastActiveDay);
    }

    // adds scroll pane, so it must not exist before the invoke
    private void remakeBy(ActiveDay activeDay) {
        String[] columnNames = { "Start", "Stop", "Mode", "Description" };
        int numberOfStates = columnNames.length;
        Object[][] data = new Object[currentActivitiesSize][numberOfStates];
        for (int r = 0; r < currentActivitiesSize; ++r) {
            for (int c = 0; c < numberOfStates; ++c) {
                ZonedActivity a = activeDay.getZonedActivity(r);
                ZonedDateTime z1 = a.getZonedStart();
                ZonedDateTime z2 = a.getZonedStop();
                int h1 = z1.getHour(), m1 = z1.getMinute(), s1 = z1.getSecond();
                int h2 = z2.getHour(), m2 = z2.getMinute(), s2 = z2.getSecond();
                if (h2 == 0 && s2 == 0) {
                    h2 = 24;
                }
                String startStr = String.format(TIME_FORMAT, h1, m1, s1);
                String stopStr = String.format(TIME_FORMAT, h2, m2, s2);
                String modeStr = a.getMode().toString();
                String descr = a.getDescr();
                data[r] = new Object[] { startStr, stopStr, modeStr, descr };
            }
        }
        tableModel = new ActiveTableModel(data, columnNames, activeDay);
        activitiesTable = new JTable(tableModel);
        activitiesTable.setEnabled(false);
        ActiveTableCellRenderer renderer = new ActiveTableCellRenderer();
        for (int i = 0; i < numberOfStates; ++i) {
            activitiesTable.getColumn(columnNames[i]).setCellRenderer(renderer);
        }
        ActiveColumnAdjuster.adjust(activitiesTable);
        scrollPane = new JScrollPane(activitiesTable);
        setTitle(currentDate.toString());
        contentPane.add(scrollPane, BorderLayout.CENTER);
        pack();
    }

    void updateBy(ActiveCalendar calendar) {
        ZonedActivity zonedActivity = calendar.getLastZonedActivity();
        LocalDate lastDate = zonedActivity.getZonedStart().toLocalDate();
        ActiveDay lastActiveDay = calendar.getActiveYear(lastDate.getYear())
                .getActiveMonth(lastDate.getMonthValue()).getActiveDay(lastDate.getDayOfMonth());
        if (!currentDate.equals(lastDate)) {
            currentDate = lastDate;
            currentActivitiesSize = lastActiveDay.getZonedActivitiesSize();
            remove(scrollPane);
            remakeBy(lastActiveDay);
            return;
        }
        for (int i = currentActivitiesSize; i < lastActiveDay.getZonedActivitiesSize(); ++i) {
            ZonedActivity a = calendar.getActiveYear(currentDate.getYear())
                    .getActiveMonth(currentDate.getMonthValue())
                    .getActiveDay(currentDate.getDayOfMonth()).getZonedActivity(i);
            ZonedDateTime z1 = a.getZonedStart();
            ZonedDateTime z2 = a.getZonedStop();
            int h1 = z1.getHour(), m1 = z1.getMinute(), s1 = z1.getSecond();
            int h2 = z2.getHour(), m2 = z2.getMinute(), s2 = z2.getSecond();
            if (h2 == 0 && s2 == 0) {
                h2 = 24;
            }
            String startStr = String.format(TIME_FORMAT, h1, m1, s1);
            String stopStr = String.format(TIME_FORMAT, h2, m2, s2);
            String modeStr = a.getMode().toString();
            String descr = a.getDescr();
            Object[] rowData = new Object[] { startStr, stopStr, modeStr, descr };
            tableModel.addRow(rowData);
            tableModel.setRowColor(tableModel.getRowCount() - 1, a.getMode());
        }
        ActiveColumnAdjuster.adjust(activitiesTable);
        currentActivitiesSize = lastActiveDay.getZonedActivitiesSize();
    }

    void setActiveDay(ActiveDay activeDay) {
        currentDate = activeDay.getFirstZonedActivity().getZonedStart().toLocalDate();
        currentActivitiesSize = activeDay.getZonedActivitiesSize();
        remove(scrollPane);
        remakeBy(activeDay);
    }
}
