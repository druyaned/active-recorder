package com.github.druyaned.active_recorder.data;

import java.io.IOException;

public final class DataFile extends DataDirFile {
    public static final String NAME = "data.xml";

    /** {@inheritDoc} */
    DataFile(DataDir dataDir) throws IOException { super(dataDir, NAME); }
}
