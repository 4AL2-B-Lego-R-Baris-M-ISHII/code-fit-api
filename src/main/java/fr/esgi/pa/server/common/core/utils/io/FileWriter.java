package fr.esgi.pa.server.common.core.utils.io;

import java.io.IOException;

public interface FileWriter {
    void writeContentToFile(String content, String filePath) throws IOException;

    void createDirectories(String directoriesStrPath) throws IOException;
}
