package com.github.druyaned.active_recorder.graphic;

import static com.github.druyaned.active_recorder.active.ActiveTime.*;
import com.github.druyaned.active_recorder.active.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import javax.swing.*;

class DaysPanel extends JPanel {
    public static final int LOW_ROWS = MAX_DAY / DAYS_IN_WEEK + 1;
    public static final int HIGH_ROWS = LOW_ROWS + 1;
    public static final int COLUMNS = DAYS_IN_WEEK;
    public static final int FONT_SIZE = 16;
    public static final String SUBTEXT_COLOR = "#505050";
    public static final Font TEXT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, FONT_SIZE);
    public static final Color LABEL_FOREGROUND_COLOR = new Color(128, 128, 224);

    private final DayFrame dayFrame;
    private final JPanel dayNamesPanel;
    private final JPanel buttonsPanel;
    private final JButton[][] dayButtons;
    private final DayButtonTasks tasks;
    private ActiveYear currentActiveYear;
    private ActiveMonth currentActiveMonth;
    private int currentActiveDaysSize;
    private int monthOffset;

    DaysPanel(ActiveCalendar calendar, DayButtonTasks tasks) {
        super(new BorderLayout());
        setOpaque(false);

        // initialize panes
        dayFrame = new DayFrame(calendar);
        dayNamesPanel = new JPanel(new GridLayout(1, COLUMNS));
        buttonsPanel = new JPanel(new GridLayout(HIGH_ROWS, COLUMNS));

        // set panes
        buttonsPanel.setOpaque(false);
        dayNamesPanel.setOpaque(false);

        // dayNamesPanel
        Color dayNameLabelColor = new Color(128, 128, 128);
        for (int d = MIN_WEEK_DAY; d <= MAX_WEEK_DAY; ++d) {
            String shortDayName = DayOfWeek.of(d).toString().substring(0, 3);
            JLabel dayNameLabel = new JLabel(shortDayName);
            dayNameLabel.setHorizontalAlignment(JLabel.CENTER);
            dayNameLabel.setForeground(dayNameLabelColor);
            dayNamesPanel.add(dayNameLabel);
        }

        // initialize current state
        currentActiveYear = calendar.getLastActiveYear();
        currentActiveMonth = currentActiveYear.getLastActiveMonth();
        currentActiveDaysSize = currentActiveMonth.getActiveDaysSize();

        // make buttons
        this.tasks = tasks;
        dayButtons = new JButton[HIGH_ROWS][COLUMNS];
        for (int r = 0; r < HIGH_ROWS; ++r) {
            for (int c = 0; c < COLUMNS; ++c) {
                dayButtons[r][c] = new JButton();
            }
        }
        remake();

        add(dayNamesPanel, BorderLayout.NORTH);
        add(buttonsPanel, BorderLayout.CENTER);
    }

//-Getters-and-setters------------------------------------------------------------------------------

    int getCurrentYear() { return currentActiveYear.number; }
    int getCurrentMonth() { return currentActiveMonth.number; }

    void setCurrent(ActiveYear activeYear, ActiveMonth activeMonth) {
        currentActiveYear = activeYear;
        currentActiveMonth = activeMonth;
        currentActiveDaysSize = activeMonth.getActiveDaysSize();
        remake();
    }

//-Methods------------------------------------------------------------------------------------------

    void dayFrameDispose() {
        if (dayFrame.isDisplayable()) {
            dayFrame.dispose();
        }
    }

    void setDayFrameInvisible() {
        if (dayFrame.isVisible()) {
            dayFrame.setVisible(false);
        }
    }

    void updateBy(ActiveCalendar calendar) {

        // declarate
        ActiveYear prevActiveYear = currentActiveYear;
        ActiveMonth prevActiveMonth = currentActiveMonth;
        int prevActiveDaysSize = currentActiveDaysSize;

        // update the current state
        currentActiveYear = calendar.getLastActiveYear();
        currentActiveMonth = currentActiveYear.getLastActiveMonth();
        currentActiveDaysSize = currentActiveMonth.getActiveDaysSize();

        // remake the panel if necessary
        if (prevActiveYear.number != currentActiveYear.number ||
            prevActiveMonth.number != currentActiveMonth.number)
        {
            remake();

            // update and set visible the day frame
            dayFrame.updateBy(calendar);
            dayFrame.setActiveDay(currentActiveMonth.getLastActiveDay());
            if (!dayFrame.isVisible()) {
                dayFrame.setVisible(true);
            }

            return;
        } // else if (prevYear == currentYear && prevMonth == currentMonth)

        // update previous active day color
        {
            int i = prevActiveDaysSize - 1;
            ActiveDay prevActiveDay = prevActiveMonth.getActiveDayBy(i);
            int buttonIndex = prevActiveDay.number - 1 + monthOffset;
            int r = buttonIndex / COLUMNS;
            int c = buttonIndex % COLUMNS;
            JButton prevDayButton = dayButtons[r][c];

            long totalActiveTime = 0;
            for (int j = 0; j < prevActiveDay.getZonedActivitiesSize(); ++j) {
                totalActiveTime += prevActiveDay.getZonedActivity(j).duration().getSeconds();
            }
            int activePart = (int) (100D * totalActiveTime / SECONDS_IN_DAY);

            String text = "<html><body><p style=\"text-align:center\">" + prevActiveDay.number +
                "<br><font size=\"2\" color=\"" + SUBTEXT_COLOR + "\">&nbsp;" +
                activePart + "%</font></p></body></html>"; // http://htmlbook.ru/css/font-size
            prevDayButton.setText(text);

            prevDayButton.setBackground(AWTColors.getBy(prevActiveDay.getColor()));
        }

        // add colors to other active days
        for (int i = prevActiveDaysSize; i < currentActiveDaysSize; ++i) {
            ActiveDay activeDay = currentActiveMonth.getActiveDayBy(i);
            int buttonIndex = activeDay.number - 1 + monthOffset;
            int r = buttonIndex / COLUMNS;
            int c = buttonIndex % COLUMNS;
            JButton dayButton = dayButtons[r][c];

            long totalActiveTime = 0;
            for (int j = 0; j < activeDay.getZonedActivitiesSize(); ++j) {
                totalActiveTime += activeDay.getZonedActivity(j).duration().getSeconds();
            }
            int activePart = (int) (100D * totalActiveTime / SECONDS_IN_DAY);

            String text = "<html><body><p style=\"text-align:center\">" + activeDay.number +
                "<br><font size=\"2\" color=\"" + SUBTEXT_COLOR + "\">&nbsp;" +
                activePart + "%</font></p></body></html>";
            dayButton.setText(text);

            dayButton.setBackground(AWTColors.getBy(activeDay.getColor()));
            addButtonAction(dayButton, activeDay);
            dayButton.setEnabled(true);
        }

        // update and set visible the day frame
        dayFrame.updateBy(calendar);
        dayFrame.setActiveDay(currentActiveMonth.getLastActiveDay());
        if (!dayFrame.isVisible()) {
            dayFrame.setVisible(true);
        }
    }

//-Private-methods----------------------------------------------------------------------------------

    private void remake() {

        // prepare button pane
        buttonsPanel.removeAll();

        // declarate and define
        int dayAmount = currentActiveMonth.getDayAmount();
        int year = currentActiveYear.number;
        int month = currentActiveMonth.number;
        int prevMonth = month - 1;
        
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        monthOffset = firstDayOfMonth.getDayOfWeek().getValue() - 1;
        int indexOfLastDay = (dayAmount - 1 + monthOffset);
        int rows = (indexOfLastDay >= LOW_ROWS * COLUMNS) ? HIGH_ROWS : LOW_ROWS;
        int prevDayAmount = (prevMonth == 0) ?
                            YearMonth.of(year, MAX_MONTH).lengthOfMonth() :
                            YearMonth.of(year, prevMonth).lengthOfMonth();

        // set layout
        // buttonsPanel.setLayout(new GridLayout(rows, COLUMNS));
        GridLayout gridLayout = (GridLayout)buttonsPanel.getLayout();
        gridLayout.setRows(rows);

        // set buttons
        int buttonIndex = 0;
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < COLUMNS; ++c, ++buttonIndex) {
                if (buttonIndex >= monthOffset && buttonIndex <= indexOfLastDay) {
                    JButton dayButton = dayButtons[r][c] = new JButton();
                    int dayNumber = buttonIndex - monthOffset + 1;
                    dayButton.setFont(TEXT_FONT);

                    ActiveDay activeDay = currentActiveMonth.getActiveDay(dayNumber);
                    if (activeDay != null) {
                        long totalActiveTime = 0;
                        for (int i = 0; i < activeDay.getZonedActivitiesSize(); ++i) {
                            totalActiveTime += activeDay.getZonedActivity(i)
                                    .duration().getSeconds();
                        }
                        int activePart = (int) (100D * totalActiveTime / SECONDS_IN_DAY);

                        String text = "<html><body><p style=\"text-align:center\">" + dayNumber +
                            "<br><font size=\"2\" color=\"" + SUBTEXT_COLOR + "\">&nbsp;" +
                            activePart + "%</font></p></body></html>";
                        dayButton.setText(text);

                        dayButton.setBackground(AWTColors.getBy(activeDay.getColor()));
                        addButtonAction(dayButton, activeDay);
                        dayButton.setEnabled(true);
                    } else { // active day doesn't exist
                        dayButton.setText(Integer.toString(dayNumber));
                        dayButton.setEnabled(false);
                    }

                    buttonsPanel.add(dayButton);
                } else { // out of the month
                    String text;
                    if (buttonIndex < monthOffset) {
                        text = Integer.toString(buttonIndex - monthOffset + prevDayAmount + 1);
                    } else { // buttonIndex > indexOfLastDay
                        text = Integer.toString(buttonIndex - monthOffset - dayAmount + 1);
                    }
                    JLabel dayLabel = new JLabel(text);
                    dayLabel.setFont(TEXT_FONT);
                    dayLabel.setForeground(LABEL_FOREGROUND_COLOR);
                    dayLabel.setHorizontalAlignment(JLabel.CENTER);

                    buttonsPanel.add(dayLabel);
                }
            }
        }
    }

    private void addButtonAction(JButton dayButton, ActiveDay activeDay) {
        ActionListener[] listeners = dayButton.getActionListeners();
        if (listeners.length == 1) { // else listeners.length == 0
            dayButton.removeActionListener(listeners[0]);
        }
        dayButton.addActionListener(new DayButtonListener(dayFrame, activeDay, tasks));
    }
}
