package fr.esgi.pa.server.code.infrastructure.device.utils;

import fr.esgi.pa.server.code.infrastructure.device.compiler.config.CompilerConfig;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import org.springframework.stereotype.Component;

@Component
public class ScriptCompilerContentImpl implements ScriptCompilerContent {

    public String getScriptByLanguage(Language language, String fileName, CompilerConfig compilerConfig) {
        if (language.getLanguageName() == LanguageName.C11) {
            return getScriptC(fileName, compilerConfig.getMemoryLimit(), compilerConfig.getTimeLimit());
        }
        return getScriptJava(fileName, compilerConfig.getMemoryLimit(), compilerConfig.getTimeLimit());
    }

    private String getScriptC(String fileName, Integer memoryLimit, Integer timeLimit) {
        return "#!/usr/bin/env bash\n" +
                "gcc " + fileName + " -o program_c" + "\n" +
                "ret=$?\n" +
                "if [ $ret -ne 0 ]\n" +
                "then\n" +
                "  exit 2\n" +
                "fi\n" +
                "ulimit -s " + memoryLimit + "\n" +
                "timeout --signal=SIGTERM " + timeLimit + " ./program_c " + "\n" +
                "exit $?\n";
    }

    private String getScriptJava(String fileName, Integer memoryLimit, Integer timeLimit) {
        return "#!/usr/bin/env bash\n" +
                "mv Main.java " + fileName + "\n" +
                "javac " + fileName + "\n" +
                "ret=$?\n" +
                "if [ $ret -ne 0 ]\n" +
                "then\n" +
                "  exit 2\n" +
                "fi\n" +
                "ulimit -s " + memoryLimit + "\n" +
                "timeout --signal=SIGTERM " + timeLimit + " java " + fileName.substring(0, fileName.length() - 5) + "\n" +
                "exit $?\n";
    }
}
