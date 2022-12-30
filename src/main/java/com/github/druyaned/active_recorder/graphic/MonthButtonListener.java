package com.github.druyaned.active_recorder.graphic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.github.druyaned.active_recorder.active.*;

class MonthButtonListener implements ActionListener {
    private final ActiveYear activeYear;
    private final ActiveMonth activeMonth;
    private final MonthButtonTasks tasks;

    MonthButtonListener(ActiveYear ay, ActiveMonth am, MonthButtonTasks t) {
        this.activeYear = ay;
        this.activeMonth = am;
        this.tasks = t;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        tasks.getDaysPanel().setCurrent(activeYear, activeMonth);
        tasks.runAll();
    }
}
