package fr.esgi.pa.server.code.infrastructure.device;

import fr.esgi.pa.server.common.core.utils.process.ProcessResult;

public interface DockerCompileRunner {
    ProcessResult start(String folderPath, String imageName, String containerName);
}
