package com.github.druyaned.active_recorder.graphic;

import java.awt.event.*;

import com.github.druyaned.active_recorder.active.ActiveDay;

public class DayButtonListener implements ActionListener {
    private final DayFrame dayFrame;
    private final ActiveDay activeDay;
    private final DayButtonTasks tasks;

    public DayButtonListener(DayFrame dayFrame, ActiveDay activeDay, DayButtonTasks tasks) {
        this.dayFrame = dayFrame;
        this.activeDay = activeDay;
        this.tasks = tasks;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dayFrame.setActiveDay(activeDay);
        tasks.runAll();
        if (!dayFrame.isVisible()) {
            dayFrame.setVisible(true);
        }
    }
}
