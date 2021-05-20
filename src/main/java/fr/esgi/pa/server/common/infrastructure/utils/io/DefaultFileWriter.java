package fr.esgi.pa.server.common.infrastructure.utils.io;

import fr.esgi.pa.server.common.core.utils.io.FileWriter;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class DefaultFileWriter implements FileWriter {
    @Override
    public void writeContentToFile(String content, String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(filePath));
        writer.write(content);
        writer.close();
    }

    @Override
    public void createDirectories(String directoriesStrPath) throws IOException {
        var directoriesPath = Paths.get(directoriesStrPath);
        Files.createDirectories(directoriesPath);
    }
}
