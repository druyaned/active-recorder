package com.github.druyaned.active_recorder.graphic;

import java.awt.*;

import javax.swing.*;

public class EmptyComp extends JComponent {
    public EmptyComp(int w, int h) {
        setOpaque(false);
        setPreferredSize(new Dimension(w, h));
    }
}
