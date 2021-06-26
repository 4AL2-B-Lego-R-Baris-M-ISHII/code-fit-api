package fr.esgi.pa.server.unit.exercise.infrastructure.dataprovider.util;

import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.utils.DefaultExerciseCaseHelperImpl;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.utils.DefaultExerciseCaseValues;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class DefaultExerciseCaseHelperImplTest {

    private DefaultExerciseCaseHelperImpl sut;

    @BeforeEach
    void setup() {
        sut = new DefaultExerciseCaseHelperImpl();
    }

    @Nested
    @DisplayName("When language is java")
    class WhenLanguageIsJava {
        private final Language javaLanguage = new Language().setId(2L)
                .setLanguageName(LanguageName.JAVA)
                .setFileExtension("java");

        @Test
        void should_return_not_null_default_exercise_values() {
            var result = sut.getValuesByLanguage(javaLanguage);

            assertThat(result).isNotNull();
            assertThat(result).isExactlyInstanceOf(DefaultExerciseCaseValues.class);
        }

        @Test
        void should_return_default_values_with_java_code_start_content() {
            var result = sut.getValuesByLanguage(javaLanguage);
            var expectedStartContent = "class Solution {\n" +
                    "    public static String exercise1(String test) {\n" +
                    "        // CODE HERE\n" +
                    "        return null;\n" +
                    "    }\n" +
                    "}\n";

            assertThat(result.getStartContent()).isEqualTo(expectedStartContent);
        }

        @Test
        void should_return_default_values_with_java_code_default_solution() {
            var result = sut.getValuesByLanguage(javaLanguage);
            var expectedSolution = "class Solution {\n" +
                    "    public static String exercise1(String test) {\n" +
                    "        // CODE HERE\n" +
                    "        return test;\n" +
                    "    }\n" +
                    "}\n";

            assertThat(result.getSolution()).isEqualTo(expectedSolution);
        }

        @Test
        void should_return_default_values_with_java_code_default_test() {
            var result = sut.getValuesByLanguage(javaLanguage);
            var expectedTest = "public class Main {\n" +
                    "    public static void main(String[] args) throws Exception {\n" +
                    "        String result = Solution.exercise1(\"toto\");\n" +
                    "        if (result == null || !result.equals(\"toto\")) {\n" +
                    "            throw new Exception(\"error expectations\");\n" +
                    "        }\n" +
                    "    }\n" +
                    "}\n";

            assertThat(result.getTestContent()).isEqualTo(expectedTest);
        }
    }

    @Nested
    @DisplayName("When language is c")
    class WhenLanguageIsC {
        private final Language cLanguage = new Language()
                .setId(1L)
                .setLanguageName(LanguageName.C)
                .setFileExtension("c");

        @Test
        void should_return_not_null_default_exercise_values() {
            var result = sut.getValuesByLanguage(cLanguage);

            assertThat(result).isNotNull();
            assertThat(result).isExactlyInstanceOf(DefaultExerciseCaseValues.class);
        }

        @Test
        void should_return_default_exercise_values_with_c_code_start_content() {
            var result = sut.getValuesByLanguage(cLanguage);
            var expectedStartContent = "#include <string.h>\n" +
                    "\n" +
                    "char* exercise1(char *test) {\n" +
                    "    // CODE HERE\n" +
                    "    return NULL;\n" +
                    "}\n";

            assertThat(result.getStartContent()).isEqualTo(expectedStartContent);
        }

        @Test
        void should_return_default_exercise_values_with_c_solution() {
            var result = sut.getValuesByLanguage(cLanguage);
            var expectedSolution = "#include <string.h>\n" +
                    "\n" +
                    "char* exercise1(char *test) {\n" +
                    "    // CODE HERE\n" +
                    "    return test;\n" +
                    "}\n";

            assertThat(result.getSolution()).isEqualTo(expectedSolution);
        }

        @Test
        void should_return_default_exercise_values_with_c_test() {
            var result = sut.getValuesByLanguage(cLanguage);
            var expectedTest = "#include <string.h>\n" +
                    "\n" +
                    "int main() {\n" +
                    "    char *result = exercise1(\"toto\");\n" +
                    "    if (strcmp(result, \"toto\") != 0) {\n" +
                    "        return 1;\n" +
                    "    }\n" +
                    "\n" +
                    "    return 0;\n" +
                    "}\n";

            assertThat(result.getTestContent()).isEqualTo(expectedTest);
        }
    }
}