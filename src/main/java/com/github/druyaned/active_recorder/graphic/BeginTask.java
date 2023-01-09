package com.github.druyaned.active_recorder.graphic;

import static com.github.druyaned.active_recorder.active.ActiveTime.*;
import java.awt.event.*;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import javax.swing.*;
import com.github.druyaned.active_recorder.active.ActiveMode;
import com.github.druyaned.active_recorder.active.ActiveTime;
import com.github.druyaned.active_recorder.active.Activity;
import java.time.Instant;

class BeginTask implements ActionListener {
    public static final int DELAY = 500;

    private final JButton begin;
    private final JButton end;
    private final JLabel display;
    private final Timer timer;
    private Instant t1 = null;
    private Instant t2 = null;
    private final Supplier<ActiveMode> modeGetter;
    private final JTextArea descrArea;
    private final IntConsumer setModeChooser; // 1 = enabled, 0 = disabled

    public BeginTask(JButton begin,
                     JButton end,
                     JLabel display,
                     Supplier<ActiveMode> modeGetter,
                     JTextArea descrArea,
                     IntConsumer setModeChooser) {
        
        this.begin = begin;
        this.end = end;
        this.display = display;
        this.descrArea = descrArea;
        ActionListener timerBeginTask = (e) -> {
            Instant currentTime = Instant.now();
            long duration = currentTime.getEpochSecond() - t1.getEpochSecond();
            int dayAsSeconds = (int) (duration % SECONDS_IN_DAY);
            int dayAsMinutes = dayAsSeconds / SECONDS_IN_MINUTE;
            int h = (int) (duration / (SECONDS_IN_MINUTE * MINUTES_IN_HOUR));
            int m = dayAsMinutes % MINUTES_IN_HOUR;
            int s = dayAsSeconds % SECONDS_IN_MINUTE;
            String text = String.format(ControlPanel.STOPWATCH_FORMAT, h, m, s);
            display.setText(text);
        };
        timer = new Timer(DELAY, timerBeginTask);
        this.modeGetter = modeGetter;
        this.setModeChooser = setModeChooser;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        t1 = Instant.now();
        begin.setEnabled(false);
        end.setEnabled(true);
        setModeChooser.accept(0); // to disable
        t2 = null;
        timer.start();
    }

    /**
     * Returns an {@link ActiveTime active time} by the selected options
     * or {@code null} if start time equals stop time.
     * 
     * @return an {@link ActiveTime active time} by the selected options
     *         or {@code null} if start time equals stop time.
     */
    Activity stopTimer() {
        t2 = Instant.now();
        begin.setEnabled(false);
        end.setEnabled(false);
        timer.stop();
        setModeChooser.accept(1);
        display.setText(String.format(ControlPanel.STOPWATCH_FORMAT, 0, 0, 0));
        if (t1.getEpochSecond() == t2.getEpochSecond()) {
            return null;
        } else {
            return new Activity(t1, t2, modeGetter.get(), descrArea.getText());
        }
    }
}
