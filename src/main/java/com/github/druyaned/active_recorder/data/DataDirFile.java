package com.github.druyaned.active_recorder.data;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;

public class DataDirFile {
    private final DataDir dataDir;
    private final String name;
    private final Path path;
    private final File file;
    public final boolean EMPTY_AT_INITIALIZATION;

    /**
     * Gives a file regarding to the {@link DataDir data dir},
     * checks the file for existence, <i><b>creates</b></i> the file if it doesn't exist.
     * 
     * @param dataDir the directory of the file.
     * @param name the name of the file (not a path).
     * @throws IOException in same cases as {@link Files#createFile(Path, FileAttribute...)}.
     */
    protected DataDirFile(DataDir dataDir, String name) throws IOException {
        this.dataDir = dataDir;
        this.name = name;
        path = Paths.get(dataDir.toString(), name);
        file = path.toFile();

        // check for existence and create if necessary
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            Files.createFile(path);
            file.setReadable(true);
            file.setWritable(true);
            EMPTY_AT_INITIALIZATION = true;
        } else {
            int firstChar;
            try (FileReader reader = new FileReader(path.toFile())) {
                firstChar = reader.read(); // -1 if the end is reached
            }
            EMPTY_AT_INITIALIZATION = firstChar == -1;
        }
    }

//-Getters------------------------------------------------------------------------------------------
    
    String getName() { return name; }
    Path asPath() { return path; }
    File asFile() { return file; }
    
//-Methods------------------------------------------------------------------------------------------

    /**
     * Returns the same as {@link Path#toString} for the file.
     * 
     * @return the same as {@link Path#toString} for the file.
     */
    @Override
    public String toString() { return path.toString(); }
}
