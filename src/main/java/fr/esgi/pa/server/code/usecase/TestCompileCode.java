package fr.esgi.pa.server.code.usecase;

import fr.esgi.pa.server.code.core.compiler.CodeResult;
import fr.esgi.pa.server.code.core.compiler.CompilerRepository;
import fr.esgi.pa.server.code.core.exception.CompilationException;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.language.core.LanguageName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestCompileCode {
    private final CompilerRepository compilerRepository;
    private final LanguageDao languageDao;

    public CodeResult execute(String codeContent, String strLanguage) throws NotFoundException, CompilationException {
        var languageName = LanguageName.valueOf(strLanguage);
        var foundLanguage = languageDao.findByLanguageName(languageName);
        var compiler = compilerRepository.findByLanguage(foundLanguage);

        try {
            return compiler.compile(codeContent, foundLanguage);
        } catch (RuntimeException exception) {
            var message = String.format(
                    "%s : Problem compilation of language '%s', cause : '%s'",
                    this.getClass(),
                    languageName,
                    exception.getMessage());
            throw new CompilationException(message);
        }
    }
}
