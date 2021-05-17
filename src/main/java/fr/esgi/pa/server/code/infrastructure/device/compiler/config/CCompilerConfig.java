package fr.esgi.pa.server.code.infrastructure.device.compiler.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class CCompilerConfig implements CompilerConfig{
    @Override
    public String getFolderPath() {
        return null;
    }

    @Override
    public String getFolderTmpPath() {
        return null;
    }

    @Override
    public Integer getTimeLimit() {
        return null;
    }

    @Override
    public Integer getMemoryLimit() {
        return null;
    }
}
