package fr.esgi.pa.server.code.infrastructure.device.compile_runner;

import fr.esgi.pa.server.code.infrastructure.device.compiler.config.CompilerConfig;
import fr.esgi.pa.server.common.core.utils.process.ProcessResult;
import fr.esgi.pa.server.language.core.Language;

public interface DockerCompileRunner {
    ProcessResult start(CompilerConfig compilerConfig, String content, Language language);
}
