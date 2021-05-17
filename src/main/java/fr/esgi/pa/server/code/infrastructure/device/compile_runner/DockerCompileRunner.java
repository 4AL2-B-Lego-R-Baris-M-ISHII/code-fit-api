package fr.esgi.pa.server.code.infrastructure.device.compile_runner;

import fr.esgi.pa.server.common.core.utils.process.ProcessResult;

public interface DockerCompileRunner {
    ProcessResult start(String folderPath, String imageName, String containerName);
}
