package fr.esgi.pa.server.code.infrastructure.device.repository;

import fr.esgi.pa.server.code.core.Compiler;
import fr.esgi.pa.server.code.infrastructure.device.compiler.CCompiler;
import fr.esgi.pa.server.code.infrastructure.device.compiler.JavaCompiler;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CompilerRepositoryImpl implements CompilerRepository {
    private final ApplicationContext context;

    @Override
    public Compiler findByLanguage(Language language) {
        return (language.getLanguageName() == LanguageName.C)
                ? context.getBean(CCompiler.class)
                : context.getBean(JavaCompiler.class);
    }
}
