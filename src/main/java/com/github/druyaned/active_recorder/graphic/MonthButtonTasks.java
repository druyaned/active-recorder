package com.github.druyaned.active_recorder.graphic;

import java.util.ArrayList;
import java.util.Arrays;

class MonthButtonTasks {
    private DaysPanel daysPanel;
    private ArrayList<Runnable> tasks; // showToDaysChanger

    public MonthButtonTasks(DaysPanel daysPanel, Runnable... tasks) {
        this.daysPanel = daysPanel;
        this.tasks = new ArrayList<>(Arrays.asList(tasks));
    }

    public DaysPanel getDaysPanel() {
        return daysPanel;
    }

    public void runAll() {
        for (Runnable task : tasks) {
            task.run();
        }
    }
}
