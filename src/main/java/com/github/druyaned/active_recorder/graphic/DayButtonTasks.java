package com.github.druyaned.active_recorder.graphic;

import java.util.ArrayList;
import java.util.Arrays;

class DayButtonTasks {
    private ArrayList<Runnable> tasks;

    public DayButtonTasks(Runnable... tasks) {
        this.tasks = new ArrayList<>(Arrays.asList(tasks));
    }

    public void runAll() {
        for (Runnable task : tasks) {
            task.run();
        }
    }
}
