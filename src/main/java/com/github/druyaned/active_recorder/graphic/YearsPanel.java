package com.github.druyaned.active_recorder.graphic;

import static com.github.druyaned.active_recorder.graphic.DaysPanel.SUBTEXT_COLOR;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

import com.github.druyaned.active_recorder.active.*;
import com.github.druyaned.active_recorder.time.Date;

class YearsPanel extends JPanel {
    public static final Font TEXT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 32);

    private final ArrayList<JButton> yearButtons = new ArrayList<>();
    private final JPanel yearButtonsPane;
    private final YearButtonTasks tasks;
    private int currentActiveYearsSize;

    YearsPanel(ActiveCalendar calendar, YearButtonTasks tasks) {

        // initialize current state
        currentActiveYearsSize = calendar.getActiveYearsSize();

        // make buttons
        yearButtonsPane = new JPanel(new GridLayout(currentActiveYearsSize, 1));
        yearButtonsPane.setOpaque(false);
        this.tasks = tasks;
        JScrollPane scrollPane = new JScrollPane(yearButtonsPane);
        for (int i = 0; i < currentActiveYearsSize; ++i) {
            final ActiveYear activeYear = calendar.getActiveYearBy(i);
            JButton yearButton = new JButton();

            yearButton.setFont(TEXT_FONT);
            String text = "<html><body><p style=\"text-align:center\">" + activeYear.number +
                "<br><font size=\"2\" color=\"" + SUBTEXT_COLOR + "\">" +
                activeYear.getActiveMonthsSize() + "/" + Date.MONTHS_IN_YEAR +
                "</font></p></body></html>";
            yearButton.setText(text);

            yearButton.setBackground(AWTColors.getBy(activeYear.getColor()));
            yearButton.addActionListener(new YearButtonListener(activeYear, tasks));
            yearButtons.add(yearButton);
            yearButtonsPane.add(yearButton);
        }

        // set the pane and add a scroll pane
        setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        yearButtonsPane.setOpaque(false);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateBy(ActiveCalendar calendar) {
        // declaration
        int prevActiveYearsSize = currentActiveYearsSize;

        // update the current state
        currentActiveYearsSize = calendar.getActiveYearsSize();

        // update color of the previous year button
        {
            int i = prevActiveYearsSize - 1;
            ActiveYear prevActiveYear = calendar.getActiveYearBy(i);
            JButton prevYearButton = yearButtons.get(i);

            String text = "<html><body><p style=\"text-align:center\">" + prevActiveYear.number +
                "<br><font size=\"2\" color=\"" + SUBTEXT_COLOR + "\">" +
                prevActiveYear.getActiveMonthsSize() + "/" + Date.MONTHS_IN_YEAR +
                "</font></p></body></html>";
            prevYearButton.setText(text);

            prevYearButton.setBackground(AWTColors.getBy(prevActiveYear.getColor()));
        }

        if (prevActiveYearsSize == currentActiveYearsSize) {
            return;
        } // else if prevActiveYearsSize != currentActiveYearsSize

        // add new year buttons
        GridLayout gridLayout = (GridLayout)yearButtonsPane.getLayout();
        gridLayout.setRows(calendar.getActiveYearsSize());
        for (int i = currentActiveYearsSize; i < calendar.getActiveYearsSize(); ++i) {
            final ActiveYear activeYear = calendar.getActiveYearBy(i);
            JButton yearButton = new JButton();

            yearButton.setFont(TEXT_FONT);
            String text = "<html><body><p style=\"text-align:center\">" + activeYear.number +
                "<br><font size=\"2\" color=\"" + SUBTEXT_COLOR + "\">" +
                activeYear.getActiveMonthsSize() + "/" + Date.MONTHS_IN_YEAR +
                "</font></p></body></html>";
            yearButton.setText(text);

            yearButton.setBackground(AWTColors.getBy(activeYear.getColor()));
            yearButton.addActionListener(new YearButtonListener(activeYear, tasks));
            yearButtons.add(yearButton);
            yearButtonsPane.add(yearButton);
        }
    }
}
