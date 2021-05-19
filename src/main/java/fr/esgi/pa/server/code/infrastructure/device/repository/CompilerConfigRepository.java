package fr.esgi.pa.server.code.infrastructure.device.repository;

import fr.esgi.pa.server.code.infrastructure.device.compiler.config.CompilerConfig;
import fr.esgi.pa.server.language.core.LanguageName;

public interface CompilerConfigRepository {
    CompilerConfig findByLanguageName(LanguageName languageName);
}
