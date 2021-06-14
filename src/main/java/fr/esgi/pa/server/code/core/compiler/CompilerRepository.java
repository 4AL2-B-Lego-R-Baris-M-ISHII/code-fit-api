package fr.esgi.pa.server.code.core.compiler;

import fr.esgi.pa.server.language.core.Language;

public interface CompilerRepository {
    Compiler findByLanguage(Language language);
}
