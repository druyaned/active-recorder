package com.github.druyaned.active_recorder.graphic;

import java.util.ArrayList;

/** The class helps to exit the app running all added exit-tasks. */
public class Exiter {
    private final ArrayList<Runnable> onExitTasks = new ArrayList<>();
    private boolean toExit = true;

//-Getters-and-setters------------------------------------------------------------------------------

    /**
     * Returns {@code true} if the {@link #runAllAndTryToExit} will immediately exit the program,
     * otherwise - {@code false}.
     * 
     * @return {@code true} if the {@link #runAllAndTryToExit} will immediately exit the program,
     *         otherwise - {@code false}.
     */
    public boolean willExit() {
        return toExit;
    }

    /** Forces the {@link #runAllAndTryToExit} to exit immediately the program. */
    public void setExit() {
        if (!toExit) {
            toExit = true;
        }
    }

    /** Forces the {@link #runAllAndTryToExit} <b>not</b> to exit immediately the program. */
    public void unsetExit() {
        if (toExit) {
            toExit = false;
        }
    }

//-Methods------------------------------------------------------------------------------------------

    public boolean add(Runnable onExitTask) {
        return onExitTasks.add(onExitTask);
    }

    /**
     * Runs all {@link #add added} {@link java.lang.Runnable tasks}
     * and {@link #willExit tries} to exit immediately the program.
     * 
     * @see #setExit
     * @see #unsetExit
     */
    public void runAllAndTryToExit() {
        for (Runnable onExitTask: onExitTasks) {
            onExitTask.run();
        }

        if (toExit) {
            System.exit(0);
        }
    }

    /** Runs all {@link #add added} {@link Runnable tasks} without exiting the program. */
    public void runAll() {
        for (Runnable onExitTask: onExitTasks) {
            onExitTask.run();
        }
    }
}
