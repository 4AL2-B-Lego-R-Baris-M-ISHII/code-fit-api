package fr.esgi.pa.server.common.infrastructure.utils.io;

import fr.esgi.pa.server.common.core.utils.io.FileFactory;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileFactoryImpl implements FileFactory {
    @Override
    public File createFile(String filePath) {
        return new File(filePath);
    }
}
