package com.github.druyaned.active_recorder.graphic;

import java.awt.*;

import javax.swing.*;

import com.github.druyaned.active_recorder.active.*;
import com.github.druyaned.active_recorder.time.*;

public class DayFrame extends JFrame {
    public static final int W = 512;
    public static final int H = 448;
    public static final int X = AppFrame.X + AppFrame.W;
    public static final int Y = AppFrame.Y;
    private static final String TIME_FORMAT = "%02d:%02d:%02d";

    private final JPanel contentPane;
    private JTable activitiesTable;
    private Date currentDate;
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

        Date lastDate = calendar.getLastActivity().getStart().getDate();
        ActiveDay lastActiveDay = calendar.getActiveYear(lastDate.year)
            .getActiveMonth(lastDate.month).getActiveDay(lastDate.day);
        currentDate = lastDate;
        currentActivitiesSize = lastActiveDay.getActivitiesSize();
        remakeBy(lastActiveDay);
    }

    // adds scroll pane, so it must not exist before the invoke
    private void remakeBy(ActiveDay activeDay) {
        String[] columnNames = { "Start", "Stop", "Mode", "ID", "Description" };
        int numberOfStates = columnNames.length;
        Object[][] data = new Object[currentActivitiesSize][numberOfStates];

        for (int r = 0; r < currentActivitiesSize; ++r) {
            for (int c = 0; c < numberOfStates; ++c) {
                Activity a = activeDay.getActivity(r);

                int h1 = a.getStart().hour, m1 = a.getStart().minute, s1 = a.getStart().second;
                int h2 = a.getStop().hour, m2 = a.getStop().minute, s2 = a.getStop().second;
                if (h2 == 0 && s2 == 0) {
                    h2 = 24;
                }
                String startStr = String.format(TIME_FORMAT, h1, m1, s1);
                String stopStr = String.format(TIME_FORMAT, h2, m2, s2);
                String modeStr = a.getMode().toString();
                String idStr = Integer.toString(a.getId());
                String descr = a.getDescription();
                data[r] = new Object[] { startStr, stopStr, modeStr, idStr, descr };
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
        Activity activity = calendar.getLastActivity();
        Date lastDate = activity.getStart().getDate();
        ActiveDay lastActiveDay = calendar.getActiveYear(lastDate.year)
            .getActiveMonth(lastDate.month).getActiveDay(lastDate.day);
        if (!currentDate.equals(lastDate)) {
            currentDate = lastDate;
            currentActivitiesSize = lastActiveDay.getActivitiesSize();

            remove(scrollPane);
            remakeBy(lastActiveDay);
            return;
        }

        for (int i = currentActivitiesSize; i < lastActiveDay.getActivitiesSize(); ++i) {
            Activity a = calendar.getActiveYear(currentDate.year)
                .getActiveMonth(currentDate.month).getActiveDay(currentDate.day).getActivity(i);

            int h1 = a.getStart().hour, m1 = a.getStart().minute, s1 = a.getStart().second;
            int h2 = a.getStop().hour, m2 = a.getStop().minute, s2 = a.getStop().second;
            if (h2 == 0 && s2 == 0) {
                h2 = 24;
            }
            String startStr = String.format(TIME_FORMAT, h1, m1, s1);
            String stopStr = String.format(TIME_FORMAT, h2, m2, s2);
            String modeStr = a.getMode().toString();
            String idStr = Integer.toString(a.getId());
            String descr = a.getDescription();
            Object[] rowData = new Object[] { startStr, stopStr, modeStr, idStr, descr };
            tableModel.addRow(rowData);
            tableModel.setRowColor(tableModel.getRowCount() - 1, a.getMode());
        }
        ActiveColumnAdjuster.adjust(activitiesTable);
        currentActivitiesSize = lastActiveDay.getActivitiesSize();
    }

    void setActiveDay(ActiveDay activeDay) {
        currentDate = activeDay.getFirstActivity().getStart().getDate();
        currentActivitiesSize = activeDay.getActivitiesSize();

        remove(scrollPane);
        remakeBy(activeDay);
    }
}
