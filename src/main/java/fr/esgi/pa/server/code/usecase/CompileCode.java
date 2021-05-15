package fr.esgi.pa.server.code.usecase;

import fr.esgi.pa.server.code.core.Code;
import fr.esgi.pa.server.code.core.CompilationException;
import fr.esgi.pa.server.code.infrastructure.device.CompilerRepository;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.language.core.LanguageName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompileCode {
    private final CompilerRepository compilerRepository;
    private final LanguageDao languageDao;

    public Code execute(String codeContent, String strLanguage) throws NotFoundException, CompilationException {
        var languageName = LanguageName.valueOf(strLanguage);
        var foundLanguage = languageDao.findByName(languageName);
        var compiler = compilerRepository.findByLanguage(foundLanguage);

        try {
            return compiler.compile(codeContent, foundLanguage, "code_" + foundLanguage.getFileExtension(), "code_container");
        } catch (RuntimeException exception) {
            var message = String.format(
                    "%s : Problem compilation of language '%s'",
                    this.getClass(),
                    languageName);
            throw new CompilationException(message);
        }
    }
}
