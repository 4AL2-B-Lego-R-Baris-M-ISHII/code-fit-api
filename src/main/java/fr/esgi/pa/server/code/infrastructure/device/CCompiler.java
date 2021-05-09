package fr.esgi.pa.server.code.infrastructure.device;

import fr.esgi.pa.server.code.core.Code;
import fr.esgi.pa.server.code.core.CodeState;
import fr.esgi.pa.server.code.core.Compiler;
import fr.esgi.pa.server.common.core.utils.io.FileReader;
import fr.esgi.pa.server.common.core.utils.io.FileWriter;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.common.core.utils.process.ProcessHelper;
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

    private static final int TIME_LIMIT = 7;
    private static final int MEMORY_LIMIT = 500;

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
        var dockerFile = C_COMPILER_FOLDER + File.separator + "Dockerfile";
        if (!fileReader.isFileExist(dockerFile)) {
            var message = String.format("%s : docker file of compiler not found", this.getClass());
            throw new FileNotFoundException(message);
        }
        var mainFile = "main." + language.getFileExtension();
        var filePath = C_COMPILER_FOLDER + File.separator + mainFile;
        fileWriter.writeContentToFile(content, filePath);
        createCLaunchScript(mainFile);

        if (!isDockerCommandWork(idCompilation)) {
            var message = String.format("%s : problem docker run", this.getClass());
            throw new RuntimeException(message);
        }

        return launchScriptAndGetResultOfCode(idCompilation, language);
    }

    private void createCLaunchScript(String mainFile) throws IOException {
        String launchScriptPath = C_COMPILER_FOLDER + File.separator + "launch.sh";
        String content = ScriptCompilerContent.getScriptC(mainFile, MEMORY_LIMIT, TIME_LIMIT);
        fileWriter.writeContentToFile(content, launchScriptPath);
    }

    private boolean isDockerCommandWork(String imageName) throws IOException, InterruptedException {
        var dockerBuildCommand = new String[]{"docker", "image", "build", C_COMPILER_FOLDER, "-t", imageName};
        var process = processHelper.createCommandProcess(dockerBuildCommand);
        return process.waitFor() == 0;
    }

    private Code launchScriptAndGetResultOfCode(String imageName, Language language) throws IOException, InterruptedException {
        var dockerRunCommand = new String[]{"docker", "run", "--rm", imageName};
        var processResult = processHelper.launchCommandProcess(dockerRunCommand);

        var status = processResult.getStatus();
        CodeState codeState = mapStatusCodeState.getOrDefault(status, CodeState.TIME_LIMIT_EXCEED);
        return new Code()
                .setLanguage(language)
                .setCodeState(codeState)
                .setOutput(processResult.getOut());
    }
}
