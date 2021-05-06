package fr.esgi.pa.server.code.infrastructure.device;

import fr.esgi.pa.server.code.core.Code;
import fr.esgi.pa.server.code.core.CodeState;
import fr.esgi.pa.server.code.core.Compiler;
import fr.esgi.pa.server.common.core.utils.io.FileReader;
import fr.esgi.pa.server.common.core.utils.io.FileWriter;
import fr.esgi.pa.server.process.core.ProcessHelper;
import fr.esgi.pa.server.language.core.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CCompiler implements Compiler {
    private final FileReader fileReader;
    private final FileWriter fileWriter;
    private final ProcessHelper processHelper;

    private static final String C_COMPILER_FOLDER = "device" + File.separator +
            "compiler" + File.separator +
            "c_compiler";

    private static final int TIME_LIMIT = 7;
    private static final int MEMORY_LIMIT = 500;

    @Override
    public Code compile(String content, Language language, String idCompilation) throws IOException, InterruptedException {
        var dockerFile = C_COMPILER_FOLDER + File.separator + "Dockerfile";
        if (!fileReader.isFileExist(dockerFile)) {
            System.out.println("Dockerfile not found");
        }
        var mainFile = "main." + language.getFileExtension();
        var filePath = C_COMPILER_FOLDER + File.separator + mainFile;
        fileWriter.writeContentToFile(content, filePath);
        createCLaunchScript(mainFile);
        if (!isDockerCommandWork(idCompilation)) {
            System.out.println("Docker run not work");
        }

        return launchScriptAndGetResultOfCode(idCompilation, language);
    }

    private void createCLaunchScript(String mainFile) throws IOException {
        String launchScriptPath = C_COMPILER_FOLDER + File.separator + "launch.sh";
        String content = ScriptCompilerContent.getScriptC(mainFile, MEMORY_LIMIT, TIME_LIMIT);
        fileWriter.writeContentToFile(content, launchScriptPath);
    }

    private boolean isDockerCommandWork(String imageName) throws IOException, InterruptedException {
        String[] dockerCommand = new String[]{"docker", "image", "build", C_COMPILER_FOLDER, "-t", imageName};
        Process process = processHelper.createCommandProcess(dockerCommand);
        return process.waitFor() == 0;
    }

    private Code launchScriptAndGetResultOfCode(String imageName, Language language) throws IOException, InterruptedException {
        var dockerRunCommand = new String[]{"docker", "run", "--rm", imageName};
        var processResult = processHelper.launchCommandProcess(dockerRunCommand);
        var status = processResult.getStatus();
        CodeState codeState;
        if (status == 0) {
            codeState = CodeState.SUCCESS;
        } else {
            if (status == 2) {
                codeState = CodeState.COMPILATION_ERROR;
            } else if (status == 1) {
                codeState = CodeState.RUNTIME_ERROR;
            } else if (status == 139) {
                codeState = CodeState.OUT_OF_MEMORY;
            } else {
                codeState = CodeState.TIME_LIMIT_EXCEED;
            }
        }
        return new Code()
                .setLanguage(language)
                .setCodeState(codeState)
                .setOutput(processResult.getOut());
    }
}
