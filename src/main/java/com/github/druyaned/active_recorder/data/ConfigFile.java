package com.github.druyaned.active_recorder.data;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;

final class ConfigFile extends DataDirFile {
    public static final String NAME = "config.dtd";
    
    /**
     * Gives a config-file regarding to the {@link DataDir data dir},
     * checks the file for existence, <i><b>creates</b></i> and writes it if necessary.
     * 
     * @param dataDir the directory of the config file.
     * @param name the name of the config file (not a path).
     * @throws IOException in same cases as {@link Files#createFile(Path, FileAttribute...)},
     *         {@link FileWriter#FileWriter(File)} and {@link Writer#write(String)}.
     */
    ConfigFile(DataDir dataDir) throws IOException {
        super(dataDir, NAME);
        
        if (EMPTY_AT_INITIALIZATION) { // so writing the config file
            final String pathName = "/data_files/config.dtd";
            try (InputStream configIn = ConfigFile.class.getResourceAsStream(pathName)) {
                Files.copy(configIn, super.asPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
