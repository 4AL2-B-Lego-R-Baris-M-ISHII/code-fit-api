package fr.esgi.pa.server.common.core.utils.io;

import java.io.IOException;

public interface FileWriter {
    public void writeContentToFile(String content, String filePath) throws IOException;
}
