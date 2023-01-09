package com.github.druyaned.active_recorder.data;

import static com.github.druyaned.active_recorder.data.StartData.*;
import java.io.*;
import java.util.Properties;
import com.github.druyaned.active_recorder.active.ActiveMode;
import java.time.Instant;

public class StartFileReader {

    /**
     * Reads the {@link StartFile property file} of the {@link Stopwatch stopwatch} start data
     * and returns read start data.
     * 
     * @param aFile property file of the stopwatch with start data to read.
     * @return {@code null} if stopwatch.started property is false,
     *         otherwise - read {@link StartData start data}.
     * @throws IOException in same cases as {@link FileReader#FileReader(File)}
     *         and {@link Properties#load(Reader)}.
     */
    public static StartData read(StartFile aFile) throws IOException {

        // read the file
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader(aFile.asFile()))) {
            properties.load(reader);
        }

        // read the start data
        boolean stopwatchStarted =
                Boolean.parseBoolean(properties.getProperty(STOPWATCH_STARTED_KEY));
        if (!stopwatchStarted) {
            return null;
        }
        Instant startLaunch = Instant.parse(properties.getProperty(START_TIME_KEY));
        ActiveMode mode = ActiveMode.valueOf((properties.getProperty(START_MODE_KEY)));
        String descr = properties.getProperty(START_DESCRIPTION_KEY);

        return new StartData(startLaunch, mode, descr);
    }
}
