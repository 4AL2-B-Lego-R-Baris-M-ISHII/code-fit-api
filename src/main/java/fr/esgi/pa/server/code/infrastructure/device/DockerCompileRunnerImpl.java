package fr.esgi.pa.server.code.infrastructure.device;

import fr.esgi.pa.server.common.core.utils.process.ProcessHelper;
import fr.esgi.pa.server.common.core.utils.process.ProcessResult;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class DockerCompileRunnerImpl implements DockerCompileRunner {
    private final ProcessHelper processHelper;

    @SneakyThrows
    @Override
    public ProcessResult start(String folderPath, String imageName, String containerName) {

        var dockerRunCommand = new String[]{"docker", "start", containerName, "-i"};
        var startResult = processHelper.launchCommandProcess(dockerRunCommand);
        if (isContainerAlreadyCreated(startResult)) {
            return startResult;
        }
        if (!isBuildImage(folderPath, imageName)) {
            var message = String.format("%s : problem docker run", this.getClass());
            throw new RuntimeException(message);
        }

        return mountContainer(folderPath, imageName, containerName);
    }

    private boolean isContainerAlreadyCreated(ProcessResult result) {
        return !result.getOut().startsWith("Error: No such container") || result.getStatus() != 1;
    }

    @SneakyThrows
    private boolean isBuildImage(String folderPath, String imageName) {
        var dockerBuildCommand = new String[]{"docker", "image", "build", folderPath, "-t", imageName};
        var process = processHelper.createCommandProcess(dockerBuildCommand);
        return process.waitFor() == 0;
    }

    @SneakyThrows
    private ProcessResult mountContainer(String folderPath, String imageName, String containerName) {
        var dockerFilePath = folderPath + File.separator + "Dockerfile";
        var dockerFile = new File(dockerFilePath);
        var absolutePath = dockerFile.getAbsolutePath().replaceFirst("Dockerfile", "") + File.separator + "tmp";
        var mountArg = "type=bind,source=" + absolutePath + ",target=/app";
        var dockerRunCommand = new String[]{"docker", "run", "--name", containerName, "--mount", mountArg, imageName};
        return processHelper.launchCommandProcess(dockerRunCommand);
    }
}
