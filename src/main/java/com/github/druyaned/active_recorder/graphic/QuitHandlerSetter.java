package com.github.druyaned.active_recorder.graphic;

import com.github.druyaned.active_recorder.graphic.Exiter;

/**
 * Sets a quit handler.
 * 
 * @since 9
 */
public class QuitHandlerSetter {

    /**
     * Sets a quit handler if the {@link java.awt.Desktop#isDesktopSupported desktop is supported}.
     * 
     * @param exiter provides exit-tasks to run it all before exiting of the program.
     * @since 9
     */
    public static void set(Exiter exiter) {
        if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            desktop.setQuitHandler((quitEvent, response) -> {
                exiter.runAll();
                response.performQuit();
            });
        }
    }
}
