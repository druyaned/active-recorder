package com.github.druyaned.active_recorder.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class DataDir {
    public static final String APP_DIR_NAME = "ActiveRecorder";

    private final String name = "active_data";
    private final Path path;
    
    /**
     * Gives a data directory regarding to the {@link #getLocationFullName running jar-file
     * location}, checks the directory for existence, <i><b>creates</b></i> the directory
     * if it doesn't exist and sets read-write permissions.
     * 
     * @throws IOException in same cases as {@link Files#createDirectory}.
     */
    protected DataDir() throws IOException {

        String userHome = System.getProperty("user.home");
        String documents = "Documents";

        // check for existence and create if necessary
        Path appDirPath = Paths.get(userHome, documents, APP_DIR_NAME);
        if (!Files.exists(appDirPath) || !Files.isDirectory(appDirPath)) {
            Files.createDirectory(appDirPath);
            File dirFile = appDirPath.toFile();
            dirFile.setReadable(true);
            dirFile.setWritable(true);
        }

        // check for existence and create if necessary
        path = Paths.get(userHome, documents, APP_DIR_NAME, name);
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            Files.createDirectory(path);
            File dirFile = path.toFile();
            dirFile.setReadable(true);
            dirFile.setWritable(true);
        }
    }

//-Getters------------------------------------------------------------------------------------------

    public String getName() { return name; }
    public Path getPath() { return path; }

//-Methods------------------------------------------------------------------------------------------

    /**
     * Returns the same as {@link Path#toString} for this data directory.
     * 
     * @return the same as {@link Path#toString} for this data directory.
     */
    @Override
    public String toString() {
        return path.toString();
    }
}
