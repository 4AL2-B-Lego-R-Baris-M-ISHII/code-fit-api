package fr.esgi.pa.server.code.infrastructure.device.compile_runner;

import fr.esgi.pa.server.common.core.utils.io.FileFactory;
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
    private final FileFactory fileFactory;

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
        var process = processHelper.launchCommandAndGetProcess(dockerBuildCommand);
        return process.waitFor() == 0;
    }

    @SneakyThrows
    private ProcessResult mountContainer(String folderPath, String imageName, String containerName) {
        var dockerFilePath = folderPath + File.separator + "Dockerfile";
        var dockerFile = fileFactory.createFile(dockerFilePath);
        var dockerFileAbsolutPath = dockerFile.getAbsolutePath();
        var absolutePath = dockerFileAbsolutPath.replaceFirst("Dockerfile", "") + "tmp";
        var mountArg = "type=bind,source=" + absolutePath + ",target=/app";
        var dockerRunCommand = new String[]{"docker", "run", "--name", containerName, "--mount", mountArg, imageName};
        return processHelper.launchCommandProcess(dockerRunCommand);
    }
}
