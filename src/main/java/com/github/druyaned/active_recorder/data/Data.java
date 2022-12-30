package com.github.druyaned.active_recorder.data;

import java.io.IOException;

public class Data {
    public final DataDir dataDir;
    public final ConfigFile configFile;
    public final DataFile dataFile;
    public final StartFile startFile;

    public Data() throws IOException {
        dataDir = new DataDir();
        configFile = new ConfigFile(dataDir);
        dataFile = new DataFile(dataDir);
        startFile = new StartFile(dataDir);
    }
}
