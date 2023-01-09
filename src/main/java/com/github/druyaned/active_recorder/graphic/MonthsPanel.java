package com.github.druyaned.active_recorder.graphic;

import static com.github.druyaned.active_recorder.active.ActiveTime.*;
import static com.github.druyaned.active_recorder.graphic.DaysPanel.SUBTEXT_COLOR;
import static com.github.druyaned.active_recorder.graphic.DaysPanel.TEXT_FONT;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.github.druyaned.active_recorder.active.*;
import java.time.Month;


class MonthsPanel extends JPanel {
    public static final int COLUMNS = 3;
    public static final int ROWS = MONTHS_IN_YEAR / COLUMNS;

    private final JButton[][] monthButtons;
    private final MonthButtonTasks tasks;
    private ActiveYear currentActiveYear;
    private int currentActiveMonthsSize;

    MonthsPanel(ActiveCalendar calendar, MonthButtonTasks tasks) {
        setLayout(new GridLayout(ROWS, COLUMNS));
        setOpaque(false);

        // initialize current state
        currentActiveYear = calendar.getLastActiveYear();
        currentActiveMonthsSize = currentActiveYear.getActiveMonthsSize();

        // make buttons
        this.tasks = tasks;
        monthButtons = new JButton[ROWS][COLUMNS];
        int monthNumber = 1;
        for (int r = 0; r < ROWS; ++r) {
            for (int c = 0; c < COLUMNS; ++c, ++monthNumber) {
                String monthName = Month.of(monthNumber).toString();
                JButton monthButton = monthButtons[r][c] = new JButton(monthName);
                monthButton.setFont(TEXT_FONT);
                monthButton.addActionListener(new MonthButtonListener(currentActiveYear,
                        currentActiveYear.getActiveMonth(monthNumber), tasks));
                add(monthButton);
            }
        }
        reset();
    }

    public void updateBy(ActiveCalendar calendar) {

        // declaration
        ActiveYear prevActiveYear = currentActiveYear;
        int prevActiveMonthSize = currentActiveMonthsSize;

        // update the current state
        currentActiveYear = calendar.getLastActiveYear();
        currentActiveMonthsSize = currentActiveYear.getActiveMonthsSize();

        // reset the panel if necessary
        if (prevActiveYear.number != currentActiveYear.number) {
            reset();
            return;
        } // else if (prevYear == currentYear)

        // update previous active month color
        {
            int i = prevActiveMonthSize - 1;
            ActiveMonth prevActiveMonth = prevActiveYear.getActiveMonthBy(i);
            int buttonIndex = prevActiveMonth.number - 1;
            int r = buttonIndex / COLUMNS;
            int c = buttonIndex % COLUMNS;
            JButton prevMonthButton = monthButtons[r][c];

            String text = "<html><body><p style=\"text-align:center\">" +
                          Month.of(prevActiveMonth.number).toString() +
                          "<br><font size=\"2\" color=\"" + SUBTEXT_COLOR + "\">" +
                          prevActiveMonth.getActiveDaysSize() + "/" +
                          prevActiveMonth.dayAmount +
                          "</font></p></body></html>";
            prevMonthButton.setText(text);
            prevMonthButton.setBackground(AWTColors.getBy(prevActiveMonth.getColor()));
        }

        // add colors to new active months
        for (int i = prevActiveMonthSize; i < prevActiveYear.getActiveMonthsSize(); ++i) {
            ActiveMonth activeMonth = prevActiveYear.getActiveMonthBy(i);
            int buttonIndex = activeMonth.number - 1;
            int r = buttonIndex / COLUMNS;
            int c = buttonIndex % COLUMNS;
            JButton monthButton = monthButtons[r][c];

            String text = "<html><body><p style=\"text-align:center\">" +
                          Month.of(activeMonth.number).toString() +
                          "<br><font size=\"2\" color=\"" + SUBTEXT_COLOR + "\">" +
                          activeMonth.getActiveDaysSize() + "/" + activeMonth.dayAmount +
                          "</font></p></body></html>";
            monthButton.setText(text);

            monthButton.setEnabled(true);
            monthButton.setBackground(AWTColors.getBy(activeMonth.getColor()));

            ActionListener[] listeners = monthButton.getActionListeners();
            monthButton.removeActionListener(listeners[0]);
            monthButton.addActionListener(new MonthButtonListener(prevActiveYear,
                    activeMonth, tasks));
        }
    }

    void setCurrent(ActiveYear activeYear) {
        currentActiveYear = activeYear;
        currentActiveMonthsSize = activeYear.getActiveMonthsSize();

        reset();
    }

    int getCurrentYear() {
        return currentActiveYear.number;
    }

//-private-methods----------------------------------------------------------------------------------

    private void reset() {
        int monthNumber = 1;
        for (int r = 0; r < ROWS; ++r) {
            for (int c = 0; c < COLUMNS; ++c, ++monthNumber) {

                JButton monthButton = monthButtons[r][c];
                ActiveMonth activeMonth = currentActiveYear.getActiveMonth(monthNumber);

                if (activeMonth != null) {
                    String text = "<html><body><p style=\"text-align:center\">" +
                                  Month.of(monthNumber).toString() +
                                  "<br><font size=\"2\" color=\"" + SUBTEXT_COLOR + "\">" +
                                  activeMonth.getActiveDaysSize() + "/" + activeMonth.dayAmount +
                                  "</font></p></body></html>";
                    monthButton.setText(text);
                    monthButton.setEnabled(true);
                    monthButton.setBackground(AWTColors.getBy(activeMonth.getColor()));

                    ActionListener[] listeners = monthButton.getActionListeners();
                    monthButton.removeActionListener(listeners[0]);
                    monthButton.addActionListener(new MonthButtonListener(currentActiveYear,
                            activeMonth, tasks));
                } else {
                    monthButton.setEnabled(false);
                }
            }
        }
    }
}
