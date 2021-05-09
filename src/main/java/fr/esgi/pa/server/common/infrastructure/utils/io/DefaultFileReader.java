package fr.esgi.pa.server.common.infrastructure.utils.io;

import fr.esgi.pa.server.common.core.utils.io.FileReader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

@Component
public class DefaultFileReader implements FileReader {
    @Override
    public Boolean isFileExist(String filePath) {
        var file = new File(filePath);
        return file.exists();
    }

    @Override
    public String readTextFile(String filePath) throws IOException {
        try {
            return Files.readString(Path.of(filePath));
        } catch (NoSuchFileException e) {
            var file = new File(filePath);
            file.createNewFile();
        }
        return "";
    }
}
