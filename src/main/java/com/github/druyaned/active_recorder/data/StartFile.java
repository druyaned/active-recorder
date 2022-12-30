package com.github.druyaned.active_recorder.data;

import java.io.IOException;

public final class StartFile extends DataDirFile {
    public static final String NAME = "properties";

    /** {@inheritDoc} */
    StartFile(DataDir dataDir) throws IOException {
        super(dataDir, NAME);
    }
}
