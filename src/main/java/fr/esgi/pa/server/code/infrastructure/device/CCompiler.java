package fr.esgi.pa.server.code.infrastructure.device;

import fr.esgi.pa.server.code.core.Code;
import fr.esgi.pa.server.code.core.CodeState;
import fr.esgi.pa.server.code.core.Compiler;
import fr.esgi.pa.server.common.core.utils.io.FileReader;
import fr.esgi.pa.server.common.core.utils.io.FileWriter;
import fr.esgi.pa.server.common.core.utils.process.ProcessHelper;
import fr.esgi.pa.server.common.core.utils.process.ProcessResult;
import fr.esgi.pa.server.language.core.Language;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CCompiler implements Compiler {
    private final FileReader fileReader;
    private final FileWriter fileWriter;
    private final ProcessHelper processHelper;
    private final Map<Integer, CodeState> mapStatusCodeState;

    public static final String C_COMPILER_FOLDER = "device" + File.separator +
            "compiler" + File.separator +
            "c_compiler";

    public static final String C_COMPILER_TEMP_FOLDER = C_COMPILER_FOLDER + File.separator + "tmp";

    private static final int TIME_LIMIT = 7;
    private static final int MEMORY_LIMIT = 500;

    @Value("${fr.esgi.pa.current.os}")
    private String currentOS;

    public CCompiler(FileReader fileReader, FileWriter fileWriter, ProcessHelper processHelper) {
        this.fileReader = fileReader;
        this.fileWriter = fileWriter;
        this.processHelper = processHelper;

        this.mapStatusCodeState = new HashMap<>();
        mapStatusCodeState.put(0, CodeState.SUCCESS);
        mapStatusCodeState.put(1, CodeState.RUNTIME_ERROR);
        mapStatusCodeState.put(2, CodeState.COMPILATION_ERROR);
        mapStatusCodeState.put(139, CodeState.OUT_OF_MEMORY);
    }

    @Override
    public Code compile(String content, Language language, String idCompilation) throws IOException, InterruptedException {
        String dockerFile = getFilePath("Dockerfile");
        if (!fileReader.isFileExist(dockerFile)) {
            var message = String.format("%s : docker file of compiler not found", this.getClass());
            throw new FileNotFoundException(message);
        }
        var mainFile = "main." + language.getFileExtension();
        String filePath = C_COMPILER_TEMP_FOLDER + File.separator + mainFile;
        fileWriter.writeContentToFile(content, filePath);
        createCLaunchScript(mainFile);

        if (!isDockerCommandWork(idCompilation)) {
            var message = String.format("%s : problem docker run", this.getClass());
            throw new RuntimeException(message);
        }

        return launchScriptAndGetResultOfCode(idCompilation, language);
    }

    private String getFilePath(String fileName) {
        return C_COMPILER_FOLDER + File.separator + fileName;
    }

    private void createCLaunchScript(String mainFile) throws IOException {
        String launchScriptPath = C_COMPILER_TEMP_FOLDER + File.separator + "launch.sh";
        String content = ScriptCompilerContent.getScriptC(mainFile, MEMORY_LIMIT, TIME_LIMIT);
        fileWriter.writeContentToFile(content, launchScriptPath);
    }

    private boolean isDockerCommandWork(String imageName) throws IOException, InterruptedException {
        var dockerImagesCommand = new String[]{"docker", "images", imageName};
        var dockerImagesResult = processHelper.launchCommandProcess(dockerImagesCommand);
        if (dockerImagesResult.getStatus() == 0 && dockerImagesResult.getOut().contains(imageName)) {
            return true;
        }
        var dockerBuildCommand = new String[]{"docker", "image", "build", C_COMPILER_FOLDER, "-t", imageName};
        var process = processHelper.createCommandProcess(dockerBuildCommand);
        return process.waitFor() == 0;
    }

    private Code launchScriptAndGetResultOfCode(String imageName, Language language) throws IOException, InterruptedException {
        var dockerFile = new File(getFilePath("Dockerfile"));
        var absolutePath = dockerFile.getAbsolutePath().replaceFirst("Dockerfile", "") + File.separator + "tmp";
        var mountArg = "type=bind,source=" + absolutePath + ",target=/app";
        var dockerPsCommand = new String[]{"docker", "container", "ls", "-a", "-q", "-f", "\"name=ccompiler_container\""};
        var dockerPsResult = processHelper.launchCommandProcess(dockerPsCommand);

        ProcessResult processResult;
        String[] dockerRunCommand;
        if (!(dockerPsResult.getOut().length() > 0)) {
            dockerRunCommand = new String[]{"docker", "run", "--name", "ccompiler_container", "--mount", mountArg, imageName};
        } else {
            dockerRunCommand = new String[]{"docker", "start", "ccompiler_container", "-i"};
        }
        processResult = processHelper.launchCommandProcess(dockerRunCommand);

        var status = processResult.getStatus();
        CodeState codeState = mapStatusCodeState.getOrDefault(status, CodeState.TIME_LIMIT_EXCEED);
        return new Code()
                .setLanguage(language)
                .setCodeState(codeState)
                .setOutput(processResult.getOut());
    }
}
