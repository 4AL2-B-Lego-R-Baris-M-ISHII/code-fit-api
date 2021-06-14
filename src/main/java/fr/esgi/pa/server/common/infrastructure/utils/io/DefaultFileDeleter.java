package fr.esgi.pa.server.common.infrastructure.utils.io;

import fr.esgi.pa.server.common.core.utils.io.FileDeleter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

@Component
public class DefaultFileDeleter implements FileDeleter {
    @Override
    public boolean removeAllFiles(String folderPath) {
        var folder = new File(folderPath);
        if (!folder.isDirectory()) return false;
        Arrays.stream(Objects.requireNonNull(folder.listFiles())).forEach(File::delete);
        return true;
    }
}
