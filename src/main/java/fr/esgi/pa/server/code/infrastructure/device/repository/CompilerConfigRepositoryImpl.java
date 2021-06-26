package fr.esgi.pa.server.code.infrastructure.device.repository;

import fr.esgi.pa.server.code.infrastructure.device.compiler.config.CCompilerConfig;
import fr.esgi.pa.server.code.infrastructure.device.compiler.config.CompilerConfig;
import fr.esgi.pa.server.code.infrastructure.device.compiler.config.JavaCompilerConfig;
import fr.esgi.pa.server.language.core.LanguageName;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CompilerConfigRepositoryImpl implements CompilerConfigRepository {
    private final ApplicationContext context;

    @Override
    public CompilerConfig findByLanguageName(LanguageName languageName) {
        return languageName == LanguageName.C11
                ? context.getBean(CCompilerConfig.class)
                : context.getBean(JavaCompilerConfig.class);
    }
}
