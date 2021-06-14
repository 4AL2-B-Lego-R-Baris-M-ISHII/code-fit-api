package fr.esgi.pa.server.code.infrastructure.device.compiler;

import fr.esgi.pa.server.code.core.compiler.CodeResult;
import fr.esgi.pa.server.code.core.compiler.CodeState;
import fr.esgi.pa.server.code.core.compiler.Compiler;
import fr.esgi.pa.server.code.infrastructure.device.compile_runner.CompileRunner;
import fr.esgi.pa.server.code.infrastructure.device.helper.CodeStateHelper;
import fr.esgi.pa.server.code.infrastructure.device.repository.CompilerConfigRepository;
import fr.esgi.pa.server.common.core.utils.io.FileDeleter;
import fr.esgi.pa.server.language.core.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JavaCompiler implements Compiler {
    private final CompileRunner compileRunner;
    private final CodeStateHelper codeStateHelper;
    private final FileDeleter fileDeleter;
    private final CompilerConfigRepository compilerConfigRepository;

    @Override
    public CodeResult compile(String content, Language language) {
        var compilerConfig = compilerConfigRepository.findByLanguageName(language.getLanguageName());
        var processResult = compileRunner.start(compilerConfig, content, language);
        CodeState codeState = codeStateHelper.getCodeState(processResult.getStatus());
        fileDeleter.removeAllFiles(compilerConfig.getFolderTmpPath());
        return new CodeResult()
                .setLanguage(language)
                .setCodeState(codeState)
                .setOutput(processResult.getOut());
    }
}
