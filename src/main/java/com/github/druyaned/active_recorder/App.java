package com.github.druyaned.active_recorder;

import java.awt.*;
import java.io.IOException;

import javax.swing.*;

import org.xml.sax.SAXException;

import com.github.druyaned.active_recorder.active.ActiveCalendar;
import com.github.druyaned.active_recorder.active.ActiveMode;
import com.github.druyaned.active_recorder.active.Activity;
import com.github.druyaned.active_recorder.data.*;
import com.github.druyaned.active_recorder.graphic.AppFrame;
import com.github.druyaned.active_recorder.graphic.ErrorFrame;
import java.time.Instant;

/**
 * This program is an attempt to solve a problem of a low life activity.
 * It helps to keep records and to view evaluation of activities.
 * Three types of actions are identified here:
 * <i>development</i>, <i>relaxation</i>, <i>maintenance</i>.
 * 
 * @author druyaned
 */
public class App {

    public static void main(String[] args) {

        // define UI style
        UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
        String appStyleName = "Nimbus";
        String styleClassName = null;
        for (int i = 0; i < infos.length; ++i) {
            if (appStyleName.equals(infos[i].getName())) {
                styleClassName = infos[i].getClassName();
                break;
            }
        }
        final String finalStyleClassName = styleClassName;

        // get data and calendar
        Data data;
        final ActiveCalendar calendar;
        try {
            data = new Data();
            if (data.dataFile.EMPTY_AT_INITIALIZATION) {
                Instant stop = Instant.now();
                Instant start = stop.minusSeconds(1);
                ActiveMode mode = ActiveMode.STAGNATION;
                String descr = "Opening ActiveRecorder";
                Activity a = new Activity(start, stop, mode, descr);
                calendar = new ActiveCalendar(new Activity[] { a });
            } else {
                calendar = new ActiveCalendar(DataFileReader.read(data.dataFile));
            }
        } catch (IOException | SAXException e) {
            final String m = "Can't read the data file but there was an effort.";
            EventQueue.invokeLater(() -> new ErrorFrame(m).setVisible(true));
            e.printStackTrace();
            return;
        }

        // run the GUI of the app
        EventQueue.invokeLater(() -> {
            JFrame frame = new AppFrame(calendar, data);
            
            if (finalStyleClassName != null) {
                try {
                    UIManager.setLookAndFeel(finalStyleClassName);
                    SwingUtilities.updateComponentTreeUI(frame);
                } catch (ClassNotFoundException |
                        IllegalAccessException |
                        InstantiationException |
                        UnsupportedLookAndFeelException exc) {
                    
                    throw new RuntimeException(exc);
                }
            }
            
            frame.setVisible(true);
        });
    }
}
