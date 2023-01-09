package com.github.druyaned.active_recorder.graphic;

import com.github.druyaned.active_recorder.active.*;
import com.github.druyaned.active_recorder.data.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import javax.swing.*;
import javax.xml.transform.TransformerException;

public class AppFrame extends JFrame {
    public static final int W = 500;
    public static final int H = 600;
    public static final int X = 100;
    public static final int Y = 100;
    public static final int DELAY = 8;
    
    private final CalendarPanel calendarPanel;
    private ControlPanel controlPanel;
    
    public AppFrame(final ActiveCalendar calendar, Data data) {
        super("ActiveRecorder");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(true);
        setLocation(X, Y);
        setPreferredSize(new Dimension(W, H));
        Exiter exiter = new Exiter();

        // setting up the content pane
        JPanel appPane = new JPanel();
        setContentPane(appPane);

        // adding the control and calendar panes
        appPane.setLayout(new BorderLayout());
        calendarPanel = new CalendarPanel(calendar);
        try {
            controlPanel = new ControlPanel(calendarPanel::take, data);
        } catch (IOException e) {
            final String m = "Can't read the start file but there was an effort.";
            invokeErrorFrameAndUnsetExit(m, e, exiter);
        }
        appPane.add(controlPanel, BorderLayout.NORTH);
        appPane.add(calendarPanel, BorderLayout.CENTER);
        pack();

        // on exit tasks
        exiter.add(() -> {
            try {
                DataFileWriter.write(calendar, data.dataFile, data.configFile);
            } catch (FileNotFoundException | TransformerException e) {
                final String m = "Can't write the data file but there was an effort.";
                invokeErrorFrameAndUnsetExit(m, e, exiter);
            }
        });
        final ControlPanel finalControlPanel = controlPanel;
        exiter.add(() -> {
            try {
                if (finalControlPanel.isStopwatchStarted()) {
                    Instant startTime = finalControlPanel.getStartTime();
                    ActiveMode mode = finalControlPanel.getMode();
                    String descr = finalControlPanel.getDescription();
                    StartData startData = new StartData(startTime, mode, descr);
                    StartFileWriter.writeIfStarted(data.startFile, startData);
                } else {
                    StartFileWriter.writeIfNotStarted(data.startFile);
                }
            } catch (IOException e) {
                final String m = "Can't write the start file but there was an effort.";
                invokeErrorFrameAndUnsetExit(m, e, exiter);
            }
        });
        exiter.add(() -> {
            if (this.isDisplayable()) {
                this.dispose();
            }
            calendarPanel.dayFrameDispose();
        });

        // save and exit on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                exiter.runAllAndTryToExit();
            }
        });

        // add close actions
        CloseActionsAdder.add(appPane, () -> exiter.runAllAndTryToExit());

        // set quit handler
        QuitHandlerSetter.set(exiter);
    }

    private void invokeErrorFrameAndUnsetExit(final String m, Exception e, Exiter exiter) {
        EventQueue.invokeLater(() -> new ErrorFrame(m).setVisible(true));
        e.printStackTrace();
        exiter.unsetExit();
    }
}
