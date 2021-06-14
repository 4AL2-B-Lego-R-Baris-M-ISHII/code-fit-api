package fr.esgi.pa.server.code.core.compiler;

import fr.esgi.pa.server.language.core.Language;

public interface Compiler {
    CodeResult compile(String content, Language language);
}
