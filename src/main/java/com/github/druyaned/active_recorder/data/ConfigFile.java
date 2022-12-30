package com.github.druyaned.active_recorder.data;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;

public final class ConfigFile extends DataDirFile {
    public static final String NAME = "config.dtd";

    private static final String[] configurations = {
        "<!ELEMENT activeTimes (item+)>",
        "<!ELEMENT item (start, stop, mode, descr)>",
        "<!ELEMENT start (year, month, day, hour, minute, second)>",
        "<!ELEMENT stop (year, month, day, hour, minute, second)>",
        "<!ELEMENT year (#PCDATA)>",
        "<!ELEMENT month (#PCDATA)>",
        "<!ELEMENT day (#PCDATA)>",
        "<!ELEMENT hour (#PCDATA)>",
        "<!ELEMENT minute (#PCDATA)>",
        "<!ELEMENT second (#PCDATA)>",
        "<!ELEMENT mode (#PCDATA)>",
        "<!ELEMENT descr (#PCDATA)>"
    };

//-Non-static---------------------------------------------------------------------------------------

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

        // write the config file
        if (EMPTY_AT_INITIALIZATION) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(super.asFile()))) {
                for (int i = 0; i < configurations.length; ++i) {
                    writer.write(configurations[i] + "\n");
                }
            }
        }
    }
}
