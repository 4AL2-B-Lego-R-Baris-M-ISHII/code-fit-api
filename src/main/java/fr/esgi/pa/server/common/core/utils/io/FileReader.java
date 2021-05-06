package fr.esgi.pa.server.common.core.utils.io;

import java.io.IOException;

public interface FileReader {
    Boolean isFileExist(String filePath);

    String readTextFile(String filePath) throws IOException;
}
