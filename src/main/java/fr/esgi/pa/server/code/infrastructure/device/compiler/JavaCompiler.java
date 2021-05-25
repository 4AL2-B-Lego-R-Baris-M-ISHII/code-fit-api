package fr.esgi.pa.server.code.infrastructure.device.compiler;

import fr.esgi.pa.server.code.core.Code;
import fr.esgi.pa.server.code.core.CodeState;
import fr.esgi.pa.server.code.core.Compiler;
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
    public Code compile(String content, Language language) {
        var compilerConfig = compilerConfigRepository.findByLanguageName(language.getLanguageName());
        var processResult = compileRunner.start(compilerConfig, content, language);
        CodeState codeState = codeStateHelper.getCodeState(processResult.getStatus());
        fileDeleter.removeAllFiles(compilerConfig.getFolderTmpPath());
        return new Code()
                .setLanguage(language)
                .setCodeState(codeState)
                .setOutput(processResult.getOut());
    }
}
