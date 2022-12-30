package com.github.druyaned.active_recorder.graphic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.github.druyaned.active_recorder.active.*;

class YearButtonListener implements ActionListener {
    private final ActiveYear activeYear;
    private final YearButtonTasks tasks;

    YearButtonListener(ActiveYear ay, YearButtonTasks t) {
        this.activeYear = ay;
        this.tasks = t;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        tasks.getMonthsPanel().setCurrent(activeYear);
        tasks.runAll();
    }
}
