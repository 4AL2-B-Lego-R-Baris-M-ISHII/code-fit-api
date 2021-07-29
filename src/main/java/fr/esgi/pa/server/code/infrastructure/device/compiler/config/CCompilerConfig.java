package fr.esgi.pa.server.code.infrastructure.device.compiler.config;

import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class CCompilerConfig implements CompilerConfig{
    private final String folderPath = System.getProperty("user.dir")+"/device" + File.separator +
            "compiler" + File.separator +
            "c_compiler";
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
        return 7;
    }

    @Override
    public Integer getMemoryLimit() {
        return 500;
    }
}
