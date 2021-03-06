package fr.esgi.pa.server.code.infrastructure.device.compiler.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Data
@Configuration
public class JavaCompilerConfig implements CompilerConfig {
    private final String folderPath = "device" + File.separator +
            "compiler" + File.separator +
            "java_compiler";
    private final String folderTmpPath = folderPath + File.separator + "tmp";
    @Override
    public String getFolderPath() {
        return folderPath;
    }

    @Override
    public String getFolderTmpPath() {
        return folderTmpPath;
    }

    @Override
    public Integer getTimeLimit() {
        return 10;
    }

    @Override
    public Integer getMemoryLimit() {
        return 1000;
    }
}
