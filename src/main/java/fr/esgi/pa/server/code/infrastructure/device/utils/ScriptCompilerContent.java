package fr.esgi.pa.server.code.infrastructure.device.utils;

import fr.esgi.pa.server.code.infrastructure.device.compiler.config.CompilerConfig;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;

public class ScriptCompilerContent {

    public static String getScriptByLanguage(Language language, String fileName, CompilerConfig compilerConfig) {
        if (language.getLanguageName() == LanguageName.C) {
            return getScriptC(fileName, compilerConfig.getMemoryLimit(), compilerConfig.getTimeLimit());
        }
        return getScriptJava(fileName, compilerConfig.getMemoryLimit(), compilerConfig.getTimeLimit());
    }

    public static String getScriptC(String fileName, Integer memoryLimit, Integer timeLimit) {
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

    public static String getScriptJava(String fileName, Integer memoryLimit, Integer timeLimit) {
        return "#!/usr/bin/env bash\n" +
                "mv main.java " + fileName + "\n" +
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
