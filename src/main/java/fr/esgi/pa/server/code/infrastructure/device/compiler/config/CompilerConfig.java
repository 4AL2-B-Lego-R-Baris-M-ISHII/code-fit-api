package fr.esgi.pa.server.code.infrastructure.device.compiler.config;

public interface CompilerConfig {
    String getFolderPath();
    String getFolderTmpPath();
    Integer getTimeLimit();
    Integer getMemoryLimit();
}
