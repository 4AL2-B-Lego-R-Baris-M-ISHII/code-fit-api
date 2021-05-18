package fr.esgi.pa.server.code.infrastructure.device.compiler.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Data
@Configuration
public class JavaCompilerConfig implements CompilerConfig {
    @Override
    public String getFolderPath() {
        return "device" + File.separator +
                "compiler" + File.separator +
                "java_compiler";
    }

    @Override
    public String getFolderTmpPath() {
        return getFolderPath() + File.separator + "tmp";
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
