package fr.esgi.pa.server.code.infrastructure.device.utils;

public class ScriptCompilerContent {
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
                "mv main.java " + fileName+ "\n" +
                "javac " + fileName + "\n" +
                "ret=$?\n" +
                "if [ $ret -ne 0 ]\n" +
                "then\n" +
                "  exit 2\n" +
                "fi\n" +
                "ulimit -s " + memoryLimit + "\n" +
                "timeout --signal=SIGTERM " + timeLimit + " java " + fileName.substring(0,fileName.length() - 5) + "\n" +
                "exit $?\n";
    }
}
