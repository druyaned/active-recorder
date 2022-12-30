package com.github.druyaned.active_recorder.graphic;

import java.awt.*;
import java.net.URL;

import javax.swing.*;

public class ErrorFrame extends JFrame {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 200;

    private static int X;
    private static int Y;
    private static final int offset = 20;

    static {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        X = screenSize.width / 2 - WIDTH / 2 - offset * 6;
        Y = screenSize.height / 2 - HEIGHT / 2 - offset * 6;
    }

    public ErrorFrame(String message) {
        super("Oops!");
        setLocation(X += offset, Y += offset);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        if (message.length() > 96) {
            throw new IllegalArgumentException("(message.length > 64): " + message.length());
        }

        // make labels
        String warningName = "/warning.png";
        URL warningURL = ErrorFrame.class.getResource(warningName);
        ImageIcon warning = new ImageIcon(warningURL);
        Image image = warning.getImage();
        int side = 64;
        Image newImage = image.getScaledInstance(side, side, Image.SCALE_SMOOTH);
        warning = new ImageIcon(newImage);
        JLabel warningLabel = new JLabel(warning, JLabel.RIGHT);
        JLabel messageLabel = new JLabel(message, JLabel.CENTER);

        // add all elements in a correct position
        JPanel contentPane = new JPanel(new GridBagLayout());
        setContentPane(contentPane);
        GBC warningGBC = new GBC(0, 0, 1, 1);
        GBC messageGBC = new GBC(1, 0, 1, 1);
        contentPane.add(warningLabel, warningGBC);
        contentPane.add(messageLabel, messageGBC);
        pack();

        // add close actions
        CloseActionsAdder.add(contentPane, () -> System.exit(0));
    }
}
