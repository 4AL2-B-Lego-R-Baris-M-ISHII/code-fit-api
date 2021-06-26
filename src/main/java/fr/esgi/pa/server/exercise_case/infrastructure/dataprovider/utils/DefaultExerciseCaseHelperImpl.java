package fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.utils;

import fr.esgi.pa.server.exercise_case.core.utils.DefaultExerciseCaseHelper;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import org.springframework.stereotype.Component;

@Component
public class DefaultExerciseCaseHelperImpl implements DefaultExerciseCaseHelper {

    @Override
    public DefaultExerciseCaseValues getValuesByLanguage(Language language) {
        if (language.getLanguageName() == LanguageName.JAVA) {
            return getJavaDefaultExerciseValues();
        }
        return getCDefaultExerciseValues();
    }

    private DefaultExerciseCaseValues getCDefaultExerciseValues() {
        return new DefaultExerciseCaseValues()
                .setStartContent("#include <string.h>\n" +
                        "\n" +
                        "char* exercise1(char *test) {\n" +
                        "    // CODE HERE\n" +
                        "    return NULL;\n" +
                        "}\n")
                .setSolution("#include <string.h>\n" +
                        "\n" +
                        "char* exercise1(char *test) {\n" +
                        "    // CODE HERE\n" +
                        "    return test;\n" +
                        "}\n")
                .setTestContent("#include <string.h>\n" +
                        "\n" +
                        "int main() {\n" +
                        "    char *result = exercise1(\"toto\");\n" +
                        "    if (strcmp(result, \"toto\") != 0) {\n" +
                        "        return 1;\n" +
                        "    }\n" +
                        "\n" +
                        "    return 0;\n" +
                        "}\n");
    }

    private DefaultExerciseCaseValues getJavaDefaultExerciseValues() {
        return new DefaultExerciseCaseValues()
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
                        "        String result = Solution.exercise1(\"toto\");\n" +
                        "        if (result == null || !result.equals(\"toto\")) {\n" +
                        "            throw new Exception(\"error expectations\");\n" +
                        "        }\n" +
                        "    }\n" +
                        "}\n");
    }
}
