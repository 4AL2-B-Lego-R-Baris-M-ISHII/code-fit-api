package fr.esgi.pa.server.integration.code.infrastructure.quality.action.by_language;

import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByC;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ActionsByCTest {
    @Autowired
    private ActionsByC sut;

    @Nested
    class GetNbLinesCodeTest {
        @Test
        void when_content_contain_zero_line_break_should_return_1() {
            var content = "int main(){return0;};";

            var result = sut.getNbLinesCode(content);

            assertThat(result).isEqualTo(1L);
        }

        @Test
        void when_content_contain_one_linux_line_break_should_return_2() {
            var content = "int main(){\nreturn0;};";

            var result = sut.getNbLinesCode(content);

            assertThat(result).isEqualTo(2L);
        }

        @Test
        void when_content_contain_one_windows_line_break_should_return_2() {
            var content = "int main(){\r\nreturn0;};";

            var result = sut.getNbLinesCode(content);

            assertThat(result).isEqualTo(2L);
        }

        @Test
        void when_content_contain_one_mac_line_break_should_return_2() {
            var content = "int main(){\rreturn0;};";

            var result = sut.getNbLinesCode(content);

            assertThat(result).isEqualTo(2L);
        }

        @Test
        void when_content_contain_3_linux_line_break_should_return_4() {
            var content = "int main(){\n\n\nreturn0;};";

            var result = sut.getNbLinesCode(content);

            assertThat(result).isEqualTo(4L);
        }
    }

    @Nested
    class GetNbLinesCommentTest {
        @Test
        void when_content_has_one_simple_comment_line_should_return_1() {
            var content = "// one comment line\n" +
                    "int main() {return 0;}";
            assertThat(sut.getNbLinesComment(content)).isEqualTo(1L);
        }

        @Test
        void when_content_has_one_line_multiple_comment_should_return_1() {
            var content = "/* one comment line*/\n" +
                    "int main() {return 0;}";
            assertThat(sut.getNbLinesComment(content)).isEqualTo(1L);
        }

        @Test
        void when_content_has_3_lines_of_one_multiple_comment_should_return_1() {
            var content = "/* one comment line\n" +
                    "second\n" +
                    "third*/\n" +
                    "int main() {return 0;}";
            assertThat(sut.getNbLinesComment(content)).isEqualTo(3L);
        }
    }

    @Nested
    class GetCyclomaticComplexityByLanguageTest {
        @Test
        void when_one_if_should_return_1() {
            var content = "int main() {" +
                    "if (0 == 0) return 0;" +
                    "return 0;" +
                    "}\n";

            assertThat(sut.getCyclomaticComplexity(content)).isEqualTo(1L);
        }

        @Test
        void when_2_if_should_return_2() {
            var content = "int main() {" +
                    "if (0 == 0) return 0;" +
                    "if (1) {" +
                    "return 1;" +
                    "}\n" +
                    "return 0;" +
                    "}\n";

            assertThat(sut.getCyclomaticComplexity(content)).isEqualTo(2L);
        }

        @Test
        void when_one_if_and_one_else_should_return_1() {
            var content = "int main() {" +
                    "if (0 == 0) return 0;" +
                    "else {" +
                    "return 1;" +
                    "}\n" +
                    "return 0;" +
                    "}\n";

            assertThat(sut.getCyclomaticComplexity(content)).isEqualTo(1L);
        }

        @Test
        void when_one_if_and_another_nested_should_return_2() {
            var content = "int main() {\n" +
                    "    int test = 0;\n" +
                    "    if (0) {\n" +
                    "        if (3) {\n" +
                    "            return 3;\n" +
                    "        }\n" +
                    "    }\n" +
                    "    return test;\n" +
                    "}";
            assertThat(sut.getCyclomaticComplexity(content)).isEqualTo(2L);
        }

        @Test
        void when_one_switch_with_one_case_should_return_1() {
            var content = "int main() {\n" +
                    "    int test = 0;\n" +
                    "    switch(1) {\n" +
                    "        case 1:\n" +
                    "            test = 1;\n" +
                    "            break;            \n" +
                    "    }\n" +
                    "    return test;\n" +
                    "}";
            assertThat(sut.getCyclomaticComplexity(content)).isEqualTo(1L);
        }

        @Test
        void when_one_switch_with_2_cases_should_return_2() {
            var content = "int main() {\n" +
                    "    int test = 0;\n" +
                    "    switch(1) {\n" +
                    "        case 1:\n" +
                    "            test = 1;\n" +
                    "            break;          \n" +
                    "        case 2:\n" +
                    "            test = 2;\n" +
                    "            break;\n" +
                    "    }\n" +
                    "    return test;\n" +
                    "}";
            assertThat(sut.getCyclomaticComplexity(content)).isEqualTo(2L);
        }

        @Test
        void when_one_for_should_return_1() {
            var content = "int main() {\n" +
                    "    int test = 0;\n" +
                    "    for (int i = 0; i < 5; i++) {\n" +
                    "        test++;\n" +
                    "    }\n" +
                    "    return test;\n" +
                    "}";
            assertThat(sut.getCyclomaticComplexity(content)).isEqualTo(1L);
        }

        @Test
        void when_one_while_should_return_1() {
            var content = "int main() {\n" +
                    "    int test = 0;\n" +
                    "    while(test == 0) {\n" +
                    "        test++;\n" +
                    "    }\n" +
                    "    return 0;\n" +
                    "}";
            assertThat(sut.getCyclomaticComplexity(content)).isEqualTo(1L);
        }

        @Test
        void when_one_do_while_should_return_1() {
            var content = "int main() {\n" +
                    "    int test = 0;\n" +
                    "    do {\n" +
                    "        test = 1;\n" +
                    "    } while(test != 0);\n" +
                    "    return 0;\n" +
                    "}";
            assertThat(sut.getCyclomaticComplexity(content)).isEqualTo(1L);
        }

        @Test
        void when_if_one_function_not_main_should_return_1() {
            var content = "int test() {\n" +
                    "    if (0) {\n" +
                    "        return 0;\n" +
                    "    }\n" +
                    "    return 1;\n" +
                    "}\n" +
                    "\n" +
                    "int main() {\n" +
                    "    test();\n" +
                    "    return 0;\n" +
                    "}";
            assertThat(sut.getCyclomaticComplexity(content)).isEqualTo(1L);
        }
    }

    @Nested
    class HasRedundantCodeTest {
        @Test
        void when_code_content_not_has_redundant_code_should_return_false() {
            var content = "int main() {\n" +
                    "    int test = 0;\n" +
                    "    \n" +
                    "    for (i = 1; i < 5; i++) {\n" +
                    "        test += i;\n" +
                    "    }\n" +
                    "    if (test == 0) {\n" +
                    "        test = 2;\n" +
                    "    }\n" +
                    "}";

            assertThat(sut.hasRedundantCode(content)).isFalse();
        }

        @Test
        void when_code_content_has_redundant_if_conditions_should_return_false() {
            var content = "int main() {\n" +
                    "    int test = 1;\n" +
                    "    \n" +
                    "    if (test == 1) {\n" +
                    "        test = 0;\n" +
                    "    }\n" +
                    "    \n" +
                    "    if (test == 1) {\n" +
                    "        test = 0;\n" +
                    "    }\n" +
                    "    \n" +
                    "    return test;\n" +
                    "}";
            assertThat(sut.hasRedundantCode(content)).isTrue();
        }

        @Test
        void when_code_contain_has_redundant_for_loops_should_return_true() {
            var content = "int main() {\n" +
                    "    int test = 0;\n" +
                    "    \n" +
                    "    for (int i = 0; i < 10; i++) {\n" +
                    "        test = i;\n" +
                    "    }\n" +
                    "    \n" +
                    "    for (int i = 0; i < 10; i++) {\n" +
                    "        test = i;\n" +
                    "    }\n" +
                    "    return 0;\n" +
                    "}";
            assertThat(sut.hasRedundantCode(content)).isTrue();
        }

        @Test
        void when_code_contain_has_redundant_if_with_one_not_same_scope_in_for_after_should_return_true() {
            var content = "int main() {\n" +
                    "    int test = 0;\n" +
                    "    \n" +
                    "    if (test == 0) {\n" +
                    "        test = 1;\n" +
                    "    }\n" +
                    "    for (int i = 0; i < 10; i++) {\n" +
                    "        if (test == 0) {\n" +
                    "            test = 1;\n" +
                    "        }\n" +
                    "    }\n" +
                    "    return 0;\n" +
                    "}";
            assertThat(sut.hasRedundantCode(content)).isTrue();
        }

        @Test
        void when_code_contain_has_redundant_if_with_one_not_same_scope_in_for_before_should_return_true() {
            var content = "int main() {\n" +
                    "    int test = 0;\n" +
                    "    for (int i = 0; i < 10; i++) {\n" +
                    "        if (test == 0) {\n" +
                    "            test = 1;\n" +
                    "        }\n" +
                    "    }\n" +
                    "        \n" +
                    "    if (test == 0) {\n" +
                    "        test = 1;\n" +
                    "    }\n" +
                    "    return 0;\n" +
                    "}";
            assertThat(sut.hasRedundantCode(content)).isTrue();
        }

        @Test
        void when_code_contains_has_redundant_functions_should_return_true() {
            var content = "#include <stdio.h>\n" +
                    "\n" +
                    "int test() {\n" +
                    "    return 1;\n" +
                    "}\n" +
                    "\n" +
                    "int test2() {\n" +
                    "    return 1;\n" +
                    "}\n" +
                    "\n" +
                    "int main() {\n" +
                    "    if (1 == 1) {\n" +
                    "        return 1;\n" +
                    "    }\n" +
                    "    return 0;\n" +
                    "}";
            assertThat(sut.hasRedundantCode(content)).isTrue();
        }

        @Test
        void when_code_contains_functions_but_not_redundant_functions_should_return_false() {
            var content = "#include <stdio.h>\n" +
                    "\n" +
                    "int test() {\n" +
                    "    return 0;\n" +
                    "}\n" +
                    "\n" +
                    "int test2() {\n" +
                    "    int test = 1;\n" +
                    "    return test;\n" +
                    "}\n" +
                    "int test3() {\n" +
                    "   return 1;\n" +
                    "}\n" +
                    "\n" +
                    "int main() {\n" +
                    "    if (1 == 1) {\n" +
                    "        return 1;\n" +
                    "    }\n" +
                    "    return 0;\n" +
                    "}";
            assertThat(sut.hasRedundantCode(content)).isFalse();
        }
    }
}