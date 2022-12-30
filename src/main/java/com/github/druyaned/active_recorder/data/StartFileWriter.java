package com.github.druyaned.active_recorder.data;

import static com.github.druyaned.active_recorder.data.StartData.*;

import java.io.*;
import java.util.Properties;

import com.github.druyaned.active_recorder.graphic.Stopwatch;

public class StartFileWriter {

    /**
     * Writes the {@link Stopwatch stopwatch} {@link StartData start data} in the
     * {@link StartFile properties file};
     * <b>WARNING</b>: is invoked only if the stopwatch has been started.
     * <i>NOTE</i>: writing the properties helps to continue
     * the countdown of the stopwatch while the app is opened again.
     * 
     * @param aFile property file of the stopwatch start data to write.
     * @param aData start data of the stopwatch to write.
     * @throws IOException in same cases as {@link FileReader#FileReader(File)}
     *         and {@link Properties#store(java.io.Writer, String)}.
     */
    public static void writeIfStarted(StartFile aFile, StartData aData) throws IOException {

        // write properties data
        Properties properties = new Properties();
        String message = "ActiveRecorder";
        properties.put(STOPWATCH_STARTED_KEY, Boolean.toString(true));
        properties.put(START_RAWSECONDS_KEY, Long.toString(aData.startRawSeconds));
        properties.put(START_MODE_KEY, aData.mode.toString());
        properties.put(START_DESCRIPTION_KEY, aData.descr);

        // write the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(aFile.asFile()))) {
            properties.store(writer, message);
        }
    }

    /**
     * Writes the {@link STOPWATCH_STARTED_KEY stopwatch.started property as false} in the
     * {@link StartFile properties file};
     * <b>WARNING</b>: is invoked only if the stopwatch has <i>NOT</i> been started.
     * <i>NOTE</i>: writing the properties helps to continue
     * the countdown of the stopwatch while the app is opened again.
     * 
     * @param aFile property file of the stopwatch start data to write.
     * @throws IOException in same cases as {@link FileReader#FileReader(File)}
     *         and {@link Properties#store(java.io.Writer, String)}.
     */
    public static void writeIfNotStarted(StartFile aFile) throws IOException {

        // write properties data
        Properties properties = new Properties();
        String message = "active_recorder";
        properties.put(STOPWATCH_STARTED_KEY, Boolean.toString(false));

        // write the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(aFile.asFile()))) {
            properties.store(writer, message);
        }
    }
}
