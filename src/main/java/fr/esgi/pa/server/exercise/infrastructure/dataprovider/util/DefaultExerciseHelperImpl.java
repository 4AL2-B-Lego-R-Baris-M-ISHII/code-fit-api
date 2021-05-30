package fr.esgi.pa.server.exercise.infrastructure.dataprovider.util;

import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import org.springframework.stereotype.Component;

@Component
public class DefaultExerciseHelperImpl implements DefaultExerciseHelper {

    @Override
    public DefaultExerciseValues getValuesByLanguage(Language language) {
        if (language.getLanguageName() == LanguageName.JAVA) {
            return getJavaDefaultExerciseValues();
        }
        return getCDefaultExerciseValues();
    }

    private DefaultExerciseValues getCDefaultExerciseValues() {
        return new DefaultExerciseValues()
                .setStartContent("char* exercise1(char *test) {\n" +
                        "    // CODE HERE\n" +
                        "    return NULL;\n" +
                        "}\n")
                .setSolution("char* exercise1(char *test) {\n" +
                        "    // CODE HERE\n" +
                        "    return test;\n" +
                        "}\n")
                .setTestContent("int main() {\n" +
                        "    char *result = exercise1(\"toto\");\n" +
                        "    if (strcmp(result, \"toto\") != 0) {\n" +
                        "        return 1;\n" +
                        "    }\n" +
                        "\n" +
                        "    return 0;\n" +
                        "}\n");
    }

    private DefaultExerciseValues getJavaDefaultExerciseValues() {
        return new DefaultExerciseValues()
                .setStartContent("class Solution {\n" +
                        "    public static String exercise1(String test) {\n" +
                        "        // CODE HERE\n" +
                        "        return null;\n" +
                        "    }\n" +
                        "}\n")
                .setSolution("class Solution {\n" +
                        "    public static String exercise1(String test) {\n" +
                        "        // CODE HERE\n" +
                        "        return test;\n" +
                        "    }\n" +
                        "}\n")
                .setTestContent("public class Main {\n" +
                        "    public static void main(String[] args) throws Exception {\n" +
                        "        var result = Solution.exercise1(\"toto\");\n" +
                        "        if (result == null || !result.equals(\"toto\")) {\n" +
                        "            throw new Exception(\"error expectations\");\n" +
                        "        }\n" +
                        "    }\n" +
                        "}\n");
    }
}
