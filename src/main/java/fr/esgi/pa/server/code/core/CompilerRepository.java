package fr.esgi.pa.server.code.core;

import fr.esgi.pa.server.language.core.Language;

public interface CompilerRepository {
    Compiler findByLanguage(Language language);
}
