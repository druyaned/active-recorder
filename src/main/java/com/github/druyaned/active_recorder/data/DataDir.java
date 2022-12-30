package com.github.druyaned.active_recorder.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class DataDir {
    public static final String APP_DIR_NAME = "ActiveRecorder";
    public static final String DATA_DIR_NAME = "active_data";
    
    private final Path DATA_DIR_PATH;
    
    /**
     * Gives a data directory regarding to the {@link #getLocationFullName running jar-file
     * location}, checks the directory for existence, <i><b>creates</b></i> the directory
     * if it doesn't exist and sets read-write permissions.
     * 
     * @throws IOException in same cases as {@link Files#createDirectory}.
     */
    protected DataDir() throws IOException {
        final String USER_HOME = System.getProperty("user.home");
        final String DOCUMENTS = "Documents";

        // check for existence and create if necessary
        Path appDirPath = Paths.get(USER_HOME, DOCUMENTS, APP_DIR_NAME);
        if (!Files.exists(appDirPath) || !Files.isDirectory(appDirPath)) {
            Files.createDirectory(appDirPath);
            File dirFile = appDirPath.toFile();
            dirFile.setReadable(true);
            dirFile.setWritable(true);
        }

        // check for existence and create if necessary
        DATA_DIR_PATH = Paths.get(USER_HOME, DOCUMENTS, APP_DIR_NAME, DATA_DIR_NAME);
        if (!Files.exists(DATA_DIR_PATH) || !Files.isDirectory(DATA_DIR_PATH)) {
            Files.createDirectory(DATA_DIR_PATH);
            File dirFile = DATA_DIR_PATH.toFile();
            dirFile.setReadable(true);
            dirFile.setWritable(true);
        }
    }

//-Getters------------------------------------------------------------------------------------------

    public Path getDataDirPath() { return DATA_DIR_PATH; }

//-Methods------------------------------------------------------------------------------------------

    /**
     * Returns the same as {@link Path#toString} for this data directory.
     * 
     * @return the same as {@link Path#toString} for this data directory.
     */
    @Override
    public String toString() { return DATA_DIR_PATH.toString(); }
}
