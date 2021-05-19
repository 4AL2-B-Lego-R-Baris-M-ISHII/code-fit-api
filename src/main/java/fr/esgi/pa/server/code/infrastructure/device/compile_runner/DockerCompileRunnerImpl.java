package fr.esgi.pa.server.code.infrastructure.device.compile_runner;

import fr.esgi.pa.server.code.infrastructure.device.compiler.config.CompilerConfig;
import fr.esgi.pa.server.code.infrastructure.device.utils.ScriptCompilerContent;
import fr.esgi.pa.server.common.core.utils.io.FileFactory;
import fr.esgi.pa.server.common.core.utils.io.FileReader;
import fr.esgi.pa.server.common.core.utils.io.FileWriter;
import fr.esgi.pa.server.common.core.utils.process.ProcessHelper;
import fr.esgi.pa.server.common.core.utils.process.ProcessResult;
import fr.esgi.pa.server.language.core.Language;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DockerCompileRunnerImpl implements DockerCompileRunner {
    private final ProcessHelper processHelper;
    private final FileFactory fileFactory;
    private final FileReader fileReader;
    private final FileWriter fileWriter;
    private final ScriptCompilerContent scriptCompilerContent;

    @SneakyThrows
    @Override
    public ProcessResult start(CompilerConfig compilerConfig, String content, Language language) {
        var folderPath = compilerConfig.getFolderPath();
        verifyIfDockerFileExists(folderPath, language);

        var folderTmpPath = compilerConfig.getFolderTmpPath();
        String mainFile = writeMainFile(folderTmpPath, content, language);
        writeScriptToRunCompiler(compilerConfig, folderTmpPath, language, mainFile);

        var containerName = String.format("code_container_%s", language.getFileExtension());
        var dockerRunCommand = new String[]{"docker", "start", containerName, "-i"};
        var startResult = processHelper.launchCommandProcess(dockerRunCommand);
        if (isContainerAlreadyCreated(startResult)) {
            return startResult;
        }
        var imageName = "code_image_" + language.getFileExtension();
        if (!isBuildImage(folderPath, imageName)) {
            var message = String.format("%s : problem docker image build", this.getClass());
            throw new RuntimeException(message);
        }

        return mountContainer(folderTmpPath, imageName, containerName);
    }

    private void verifyIfDockerFileExists(String folderPath, Language language) throws FileNotFoundException {
        var dockerFile = getFilePath(folderPath, "Dockerfile");
        if (!fileReader.isFileExist(dockerFile)) {
            var message = String.format("%s : docker file of compiler '%s' not found", this.getClass(), language.getLanguageName());
            throw new FileNotFoundException(message);
        }
    }

    private String writeMainFile(String folderTmpPath, String content, Language language) throws IOException {
        var mainFile = "main." + language.getFileExtension();
        var filePath = getFilePath(folderTmpPath, mainFile);
        fileWriter.writeContentToFile(content, filePath);
        return mainFile;
    }

    private void writeScriptToRunCompiler(CompilerConfig compilerConfig, String folderTmpPath, Language language, String mainFile) throws IOException {
        String launchScriptPath = getFilePath(folderTmpPath, "launch.sh");
        String scriptContent = scriptCompilerContent.getScriptByLanguage(language, mainFile, compilerConfig);
        fileWriter.writeContentToFile(scriptContent, launchScriptPath);
    }


    private String getFilePath(String folderPath, String fileName) {
        return folderPath + File.separator + fileName;
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
    private ProcessResult mountContainer(String folderTmpPath, String imageName, String containerName) {
        var absolutePath = fileFactory.createFile(folderTmpPath).getAbsolutePath();
        var mountArg = "type=bind,source=" + absolutePath + ",target=/app";
        var dockerRunCommand = new String[]{"docker", "run", "--name", containerName, "--mount", mountArg, imageName};

        return processHelper.launchCommandProcess(dockerRunCommand);
    }
}
