package fr.esgi.pa.server.code.infrastructure.device;

import fr.esgi.pa.server.code.core.Code;
import fr.esgi.pa.server.code.core.CodeState;
import fr.esgi.pa.server.code.core.Compiler;
import fr.esgi.pa.server.code.infrastructure.device.helper.CodeStateHelper;
import fr.esgi.pa.server.code.infrastructure.device.utils.ScriptCompilerContent;
import fr.esgi.pa.server.common.core.utils.io.FileDeleter;
import fr.esgi.pa.server.common.core.utils.io.FileReader;
import fr.esgi.pa.server.common.core.utils.io.FileWriter;
import fr.esgi.pa.server.language.core.Language;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CCompiler implements Compiler {
    private final FileReader fileReader;
    private final FileWriter fileWriter;
    private final FileDeleter fileDeleter;
    private final DockerCompileRunner dockerCompileRunner;
    private final CodeStateHelper codeStateHelper;

    public static final String C_COMPILER_FOLDER = "device" + File.separator +
            "compiler" + File.separator +
            "c_compiler";

    public static final String C_COMPILER_TEMP_FOLDER = C_COMPILER_FOLDER + File.separator + "tmp";

    private static final int TIME_LIMIT = 7;
    private static final int MEMORY_LIMIT = 500;

    @SneakyThrows
    @Override
    public Code compile(String content, Language language, String imageName, String containerName) {
        String dockerFile = getFilePath(C_COMPILER_FOLDER, "Dockerfile");
        if (!fileReader.isFileExist(dockerFile)) {
            var message = String.format("%s : docker file of compiler not found", this.getClass());
            throw new FileNotFoundException(message);
        }
        var mainFile = "main." + language.getFileExtension();
        String filePath = getFilePath(C_COMPILER_TEMP_FOLDER, mainFile);
        fileWriter.writeContentToFile(content, filePath);
        createCLaunchScript(mainFile);

        var processResult = dockerCompileRunner.start(C_COMPILER_FOLDER, imageName, containerName);
        fileDeleter.removeAllFiles(C_COMPILER_TEMP_FOLDER);
        CodeState codeState = codeStateHelper.getCodeState(processResult.getStatus());
        return new Code()
                .setLanguage(language)
                .setCodeState(codeState)
                .setOutput(processResult.getOut());
    }

    private String getFilePath(String folderPath, String fileName) {
        return folderPath + File.separator + fileName;
    }

    private void createCLaunchScript(String mainFile) throws IOException {
        String launchScriptPath = C_COMPILER_TEMP_FOLDER + File.separator + "launch.sh";
        String content = ScriptCompilerContent.getScriptC(mainFile, MEMORY_LIMIT, TIME_LIMIT);
        fileWriter.writeContentToFile(content, launchScriptPath);
    }
}
