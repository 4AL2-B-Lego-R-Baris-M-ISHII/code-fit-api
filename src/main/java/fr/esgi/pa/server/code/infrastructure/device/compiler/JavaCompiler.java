package fr.esgi.pa.server.code.infrastructure.device.compiler;

import fr.esgi.pa.server.code.core.Code;
import fr.esgi.pa.server.code.core.CodeState;
import fr.esgi.pa.server.code.core.Compiler;
import fr.esgi.pa.server.code.infrastructure.device.compile_runner.DockerCompileRunner;
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
public class JavaCompiler implements Compiler {
    private final FileReader fileReader;
    private final FileWriter fileWriter;
    private final FileDeleter fileDeleter;
    private final DockerCompileRunner dockerCompileRunner;
    private final CodeStateHelper codeStateHelper;

    public static final String JAVA_COMPILER_FOLDER = "device" + File.separator +
            "compiler" + File.separator +
            "java_compiler";
    public static final String JAVA_COMPILER_TEMP_FOLDER = JAVA_COMPILER_FOLDER + File.separator + "tmp";

    private static final int TIME_LIMIT = 10;
    private static final int MEMORY_LIMIT = 1000;

    @SneakyThrows
    @Override
    public Code compile(String content, Language language, String imageName, String containerName) {
        String dockerFile = getFilePath(JAVA_COMPILER_FOLDER, "Dockerfile");
        if (!fileReader.isFileExist(dockerFile)) {
            var message = String.format("%s : docker file of compiler not found", this.getClass());
            throw new FileNotFoundException(message);
        }
        var mainFile = "main." + language.getFileExtension();
        String filePath = getFilePath(JAVA_COMPILER_TEMP_FOLDER, mainFile);
        fileWriter.writeContentToFile(content, filePath);
        createJavaLaunchScript(mainFile);

        var processResult = dockerCompileRunner.start(JAVA_COMPILER_FOLDER, imageName, containerName);
        fileDeleter.removeAllFiles(JAVA_COMPILER_TEMP_FOLDER);
        CodeState codeState = codeStateHelper.getCodeState(processResult.getStatus());
        return new Code()
                .setLanguage(language)
                .setCodeState(codeState)
                .setOutput(processResult.getOut());
    }

    private void createJavaLaunchScript(String mainFile) throws IOException {
        String launchScriptPath = JAVA_COMPILER_TEMP_FOLDER + File.separator + "launch.sh";
        String content = ScriptCompilerContent.getScriptJava(mainFile, MEMORY_LIMIT, TIME_LIMIT);

        fileWriter.writeContentToFile(content, launchScriptPath);
    }

    private String getFilePath(String folderPath, String fileName) {
        return folderPath + File.separator + fileName;
    }
}
