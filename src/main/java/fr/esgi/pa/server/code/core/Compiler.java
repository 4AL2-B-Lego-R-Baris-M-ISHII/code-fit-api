package fr.esgi.pa.server.code.core;

import fr.esgi.pa.server.language.core.Language;

import java.io.IOException;

public interface Compiler {
    Code compile(String content, Language language, String imageName, String containerName);
}
