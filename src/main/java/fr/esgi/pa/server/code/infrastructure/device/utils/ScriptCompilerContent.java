package fr.esgi.pa.server.code.infrastructure.device.utils;

import fr.esgi.pa.server.code.infrastructure.device.compiler.config.CompilerConfig;
import fr.esgi.pa.server.language.core.Language;

public interface ScriptCompilerContent {
    String getScriptByLanguage(Language language, String fileName, CompilerConfig compilerConfig);
}
