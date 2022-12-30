package com.github.druyaned.active_recorder.graphic;

import java.awt.event.*;

import javax.swing.*;

public class CloseActionsAdder {

    public static void add(JPanel pane, Runnable closeTask) {

        // declare
        InputMap inputMap = pane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = pane.getActionMap();
        AbstractAction closeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                closeTask.run();
            }
        };

        // add close action [cmd + w]
        KeyStroke closeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.META_DOWN_MASK);
        String closeKey = "close.with.meta";
        inputMap.put(closeStroke, closeKey);
        actionMap.put(closeKey, closeAction);

        // add close action [ctrl + w]
        closeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK);
        closeKey = "close.with.ctrl";
        inputMap.put(closeStroke, closeKey);
        actionMap.put(closeKey, closeAction);
    }

    public static void addQuitAction(JPanel pane, Runnable closeTask) {

        // declare
        InputMap inputMap = pane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = pane.getActionMap();
        AbstractAction closeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                closeTask.run();
            }
        };

        // add close action [cmd + w]
        KeyStroke closeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.META_DOWN_MASK);
        String closeKey = "close.with.meta";
        inputMap.put(closeStroke, closeKey);
        actionMap.put(closeKey, closeAction);

        // add close action [ctrl + w]
        closeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK);
        closeKey = "close.with.ctrl";
        inputMap.put(closeStroke, closeKey);
        actionMap.put(closeKey, closeAction);
    }
}
