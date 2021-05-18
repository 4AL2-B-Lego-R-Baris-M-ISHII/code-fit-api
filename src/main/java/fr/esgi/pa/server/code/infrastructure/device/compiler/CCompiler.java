package fr.esgi.pa.server.code.infrastructure.device.compiler;

import fr.esgi.pa.server.code.core.Code;
import fr.esgi.pa.server.code.core.CodeState;
import fr.esgi.pa.server.code.core.Compiler;
import fr.esgi.pa.server.code.infrastructure.device.compile_runner.DockerCompileRunner;
import fr.esgi.pa.server.code.infrastructure.device.compiler.config.CCompilerConfig;
import fr.esgi.pa.server.code.infrastructure.device.compiler.config.JavaCompilerConfig;
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
    private final FileDeleter fileDeleter;
    private final DockerCompileRunner dockerCompileRunner;
    private final CodeStateHelper codeStateHelper;

    @Override
    public Code compile(String content, Language language, String imageName, String containerName) {
        var compilerConfig = new CCompilerConfig();
        var processResult = dockerCompileRunner.start(compilerConfig, content, language);
        CodeState codeState = codeStateHelper.getCodeState(processResult.getStatus());
        fileDeleter.removeAllFiles(compilerConfig.getFolderTmpPath());
        return new Code()
                .setLanguage(language)
                .setCodeState(codeState)
                .setOutput(processResult.getOut());
    }
}
