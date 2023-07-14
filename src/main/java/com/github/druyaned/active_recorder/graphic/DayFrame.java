package com.github.druyaned.active_recorder.graphic;

import com.github.druyaned.active_recorder.active.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import javax.swing.*;

public class DayFrame extends JFrame {
    
    public static final int W = 512;
    public static final int H = 448;
    public static final int X = AppFrame.X + AppFrame.W;
    public static final int Y = AppFrame.Y;
    private static final String TIME_FORMAT = "%02d:%02d:%02d";
    private final JPanel contentPane = new JPanel(new BorderLayout());
    private final JPanel tablePane = new JPanel(new BorderLayout());
    private final JPanel statsPane = new JPanel();
    private final JLabel developmentLabel = new JLabel();
    private final JLabel stagnationLabel = new JLabel();
    private final JLabel relaxationLabel = new JLabel();
    private Duration developmentDuration;
    private Duration stagnationDuration;
    private Duration relaxationDuration;
    private JTable activitiesTable;
    private LocalDate currentDate;
    private int currentActivitiesSize;
    private ActiveTableModel tableModel;
    private JScrollPane scrollPane;
    
//-Constructors-------------------------------------------------------------------------------------
    
    public DayFrame(ActiveCalendar calendar) {
        setLocation(X, Y);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setPreferredSize(new Dimension(W, H));
        // setting up the content pane
        setContentPane(contentPane);
        CloseActionsAdder.add(contentPane, () -> {
            if (isVisible()) {
                setVisible(false);
            }
        });
        // defining the dates and the size
        LocalDate lastDate = calendar.getLastZonedActivity().getZonedStart().toLocalDate();
        ActiveDay lastActiveDay = calendar
                .getActiveYear(lastDate.getYear())
                .getActiveMonth(lastDate.getMonthValue())
                .getActiveDay(lastDate.getDayOfMonth());
        currentDate = lastDate;
        currentActivitiesSize = lastActiveDay.getZonedActivitiesSize();
        // creating the statsPane
        developmentLabel.setOpaque(true);
        stagnationLabel.setOpaque(true);
        relaxationLabel.setOpaque(true);
        developmentLabel.setBackground(AWTColors.getBy(ActiveColor.DEVELOPMENT_COLOR));
        stagnationLabel.setBackground(AWTColors.getBy(ActiveColor.STAGNATION_COLOR));
        relaxationLabel.setBackground(AWTColors.getBy(ActiveColor.RELAXATION_COLOR));
        statsPane.add(developmentLabel);
        statsPane.add(stagnationLabel);
        statsPane.add(relaxationLabel);
        // adding panes to the content pane and remaking them
        contentPane.add(tablePane, BorderLayout.CENTER);
        contentPane.add(statsPane, BorderLayout.SOUTH);
        remakeBy(lastActiveDay);
    }
    
//-Private-methods----------------------------------------------------------------------------------
    
    private void updateStats(ActiveMode mode, Duration activityDuration) {
        switch (mode) {
            case DEVELOPMENT -> {
                developmentDuration = developmentDuration.plus(activityDuration);
                long developmentSeconds = developmentDuration.getSeconds();
                int h = (int)(developmentSeconds / 3600);
                int m = (int)((developmentSeconds / 60) % 60);
                int s = (int)(developmentSeconds % 60);
                developmentLabel.setText(String.format(TIME_FORMAT, h, m, s));
            }
            case STAGNATION -> {
                stagnationDuration = stagnationDuration.plus(activityDuration);
                long stagnationSeconds = stagnationDuration.getSeconds();
                int h = (int)(stagnationSeconds / 3600);
                int m = (int)((stagnationSeconds / 60) % 60);
                int s = (int)(stagnationSeconds % 60);
                stagnationLabel.setText(String.format(TIME_FORMAT, h, m, s));
            }
            case RELAXATION -> {
                relaxationDuration = relaxationDuration.plus(activityDuration);
                long relaxationSeconds = relaxationDuration.getSeconds();
                int h = (int)(relaxationSeconds / 3600);
                int m = (int)((relaxationSeconds / 60) % 60);
                int s = (int)(relaxationSeconds % 60);
                relaxationLabel.setText(String.format(TIME_FORMAT, h, m, s));
            }
            default -> throw new IllegalArgumentException("wrong activeMode");
        }
    }
    
    // adds scroll pane, so it must not exist before the invoke
    private void remakeBy(ActiveDay activeDay) {
        String[] columnNames = { "Start", "Stop", "Mode", "Description" };
        int numberOfStates = columnNames.length;
        Object[][] data = new Object[currentActivitiesSize][numberOfStates];
        developmentDuration = Duration.ofMillis(0L);
        stagnationDuration = Duration.ofMillis(0L);
        relaxationDuration = Duration.ofMillis(0L);
        for (int r = 0; r < currentActivitiesSize; ++r) {
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
            // changing the statsPane
            updateStats(a.getMode(), Duration.between(z1, z2));
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
        tablePane.add(scrollPane, BorderLayout.CENTER);
        pack();
    }
    
//-Methods------------------------------------------------------------------------------------------

    void updateBy(ActiveCalendar calendar) {
        ZonedActivity zonedActivity = calendar.getLastZonedActivity();
        LocalDate lastDate = zonedActivity.getZonedStart().toLocalDate();
        ActiveDay lastActiveDay = calendar
                .getActiveYear(lastDate.getYear())
                .getActiveMonth(lastDate.getMonthValue())
                .getActiveDay(lastDate.getDayOfMonth());
        // if the day has been changed
        if (!currentDate.equals(lastDate)) {
            currentDate = lastDate;
            currentActivitiesSize = lastActiveDay.getZonedActivitiesSize();
            tablePane.remove(scrollPane);
            remakeBy(lastActiveDay);
            return;
        }
        // else if the day hasn't been changed
        for (int i = currentActivitiesSize; i < lastActiveDay.getZonedActivitiesSize(); ++i) {
            ZonedActivity a = calendar
                    .getActiveYear(currentDate.getYear())
                    .getActiveMonth(currentDate.getMonthValue())
                    .getActiveDay(currentDate.getDayOfMonth())
                    .getZonedActivity(i);
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
            // changing the statsPane
            updateStats(a.getMode(), Duration.between(z1, z2));
        }
        ActiveColumnAdjuster.adjust(activitiesTable);
        currentActivitiesSize = lastActiveDay.getZonedActivitiesSize();
    }

    void setActiveDay(ActiveDay activeDay) {
        currentDate = activeDay.getFirstZonedActivity().getZonedStart().toLocalDate();
        currentActivitiesSize = activeDay.getZonedActivitiesSize();
        tablePane.remove(scrollPane);
        remakeBy(activeDay);
    }
    
}
