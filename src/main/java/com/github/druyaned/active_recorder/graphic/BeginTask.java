package com.github.druyaned.active_recorder.graphic;

import static com.github.druyaned.active_recorder.time.DateTime.*;

import java.awt.event.*;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

import javax.swing.*;

import com.github.druyaned.active_recorder.active.ActiveMode;
import com.github.druyaned.active_recorder.active.ActiveTime;
import com.github.druyaned.active_recorder.time.DateTime;

class BeginTask implements ActionListener {
    public static final int DELAY = 500;

    private JButton begin;
    private JButton end;
    private JLabel display;
    private Timer timer;
    private DateTime t1 = null;
    private DateTime t2 = null;
    private Supplier<ActiveMode> modeGetter;
    private JTextArea descrArea;
    private IntConsumer setModeChooser; // 1 = enabled, 0 = disabled

    public BeginTask(JButton begin, JButton end, JLabel display,
        Supplier<ActiveMode> modeGetter, JTextArea descrArea, IntConsumer setModeChooser)
    {
        this.begin = begin;
        this.end = end;
        this.display = display;
        this.descrArea = descrArea;
        ActionListener timerBeginTask = (e) -> {
            DateTime currentTime = DateTime.now();
            long duration = currentTime.rawSeconds - t1.rawSeconds;

            int dayAsSeconds = (int) (duration % SECONDS_IN_DAY);
            int dayAsMinutes = dayAsSeconds / SECONDS_IN_MINUTE;
            int h = (int) (duration / (SECONDS_IN_MINUTE * MINUTES_IN_HOUR));
            int m = dayAsMinutes % MINUTES_IN_HOUR;
            int s = dayAsSeconds % SECONDS_IN_MINUTE;

            String text = String.format(ControlPanel.STOPWATCH_DISPLAY_FORMAT, h, m, s);
            display.setText(text);
        };
        timer = new Timer(DELAY, timerBeginTask);
        this.modeGetter = modeGetter;
        this.setModeChooser = setModeChooser;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        t1 = DateTime.now();
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
    ActiveTime stopTimer() {
        t2 = DateTime.now();
        begin.setEnabled(false);
        end.setEnabled(false);
        timer.stop();
        setModeChooser.accept(1);
        display.setText(String.format(ControlPanel.STOPWATCH_DISPLAY_FORMAT, 0, 0, 0));
        if (t1.rawSeconds == t2.rawSeconds) {
            return null;
        } else {
            return new ActiveTime(t1, t2, modeGetter.get(), descrArea.getText());
        }
    }
}
