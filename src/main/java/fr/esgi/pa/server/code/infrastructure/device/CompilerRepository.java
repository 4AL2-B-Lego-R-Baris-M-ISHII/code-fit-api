package fr.esgi.pa.server.code.infrastructure.device;

import fr.esgi.pa.server.code.core.Compiler;
import fr.esgi.pa.server.language.core.Language;

public interface CompilerRepository {
    Compiler findByLanguage(Language language);
}
