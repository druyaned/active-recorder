package com.github.druyaned.active_recorder.graphic;

import java.util.ArrayList;
import java.util.Arrays;

class YearButtonTasks {
    private MonthsPanel monthsPanel;
    private ArrayList<Runnable> tasks; // showToMonthsChanger

    public YearButtonTasks(MonthsPanel monthsPanel, Runnable... tasks) {
        this.monthsPanel = monthsPanel;
        this.tasks = new ArrayList<>(Arrays.asList(tasks));
    }

    public MonthsPanel getMonthsPanel() {
        return monthsPanel;
    }

    public void runAll() {
        for (Runnable task : tasks) {
            task.run();
        }
    }
}
