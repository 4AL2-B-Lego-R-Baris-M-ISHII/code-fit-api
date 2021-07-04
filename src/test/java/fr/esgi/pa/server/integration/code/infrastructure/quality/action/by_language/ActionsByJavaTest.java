package fr.esgi.pa.server.integration.code.infrastructure.quality.action.by_language;

import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByJava;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ActionsByJavaTest {
    @Autowired
    private ActionsByJava sut;

    @Nested
    class GetNbLinesCodeTest {
        @Test
        void when_no_line_break_should_return_1() {
            var content = "class Solution {" +
                    "    public static String exercise1(String test) {" +
                    "        return null;" +
                    "    }" +
                    "}";

            var result = sut.getNbLinesCode(content);

            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(1L);
        }

        @Test
        void when_4_line_break_should_return_5() {
            var content = "class Solution {\n" +
                    "    public static String exercise1(String test) {\n" +
                    "        return null;\n" +
                    "    }\n" +
                    "}";

            var result = sut.getNbLinesCode(content);

            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(5L);
        }
    }

    @Nested
    class GetNbLinesCommentTest {
        @Test
        void when_no_comment_should_return_0() {
            var noCommentContent = "class Solution {\n" +
                    "    public static String exercise1(String test) {\n" +
                    "        return null;\n" +
                    "    }\n" +
                    "}";

            assertThat(sut.getNbLinesComment(noCommentContent)).isEqualTo(0L);
        }

        @Test
        void when_1_simple_comment_should_return_1() {
            var oneSimpleComment = "// one comment\n" +
                    "class Solution {\n" +
                    "    public static String exercise1(String test) {\n" +
                    "        return null;\n" +
                    "    }\n" +
                    "}";
            assertThat(sut.getNbLinesComment(oneSimpleComment)).isEqualTo(1L);
        }

        @Test
        void when_1_line_multiple_comment_on_code_line_should_return_1() {
            var multipleComment = "class Solution {/* one line multiple comment*/\n" +
                    "    public static String exercise1(String test) {\n" +
                    "        return null;\n" +
                    "    }\n" +
                    "}";
            assertThat(sut.getNbLinesComment(multipleComment)).isEqualTo(1L);
        }

        @Test
        void when_1_line_multiple_comment_should_return_1() {
            var multipleComment = "/* one line multiple comment*/\n" +
                    "class Solution {\n" +
                    "    public static String exercise1(String test) {\n" +
                    "        return null;\n" +
                    "    }\n" +
                    "}";
            assertThat(sut.getNbLinesComment(multipleComment)).isEqualTo(1L);
        }

        @Test
        void when_3_line_multiple_comment_should_return_3() {
            var multipleComment = "/* first line\n" +
                    "second line\n" +
                    "third line*/\n" +
                    "class Solution {\n" +
                    "    public static String exercise1(String test) {\n" +
                    "        return null;\n" +
                    "    }\n" +
                    "}";
            assertThat(sut.getNbLinesComment(multipleComment)).isEqualTo(3L);
        }
    }

    @Nested
    class GetCyclomaticComplexityTest {
        @Test
        void when_one_if_should_return_1() {
            var content = "public class Main { \n" +
                    "   public static void main(String[] args) { \n" +
                    "      if (true) {\n" +
                    "        return;\n" +
                    "      }\n" +
                    "   }\n" +
                    "}";

            assertThat(sut.getCyclomaticComplexity(content)).isEqualTo(1L);
        }

        @Test
        void when_2_case_should_return_1() {
            var content = "public class Main { \n" +
                    "   public static void main(String[] args) {\n" +
                    "      boolean test = false; \n" +
                    "      switch(true) {\n" +
                    "        case true:\n" +
                    "            test = true;\n" +
                    "            break;\n" +
                    "        case false:\n" +
                    "            test = false;\n" +
                    "            break;\n" +
                    "      }\n" +
                    "   }\n" +
                    "}";

            assertThat(sut.getCyclomaticComplexity(content)).isEqualTo(2L);
        }

        @Test
        void when_one_for_should_return_1() {
            var content = "public class Main { \n" +
                    "   public static void main(String[] args) {\n" +
                    "      for (int i = 0; i < 3; i++) {\n" +
                    "        System.out.println(\"test\");\n" +
                    "      }\n" +
                    "   }\n" +
                    "}";
            assertThat(sut.getCyclomaticComplexity(content)).isEqualTo(1L);
        }

        @Test
        void when_one_while_should_return_1() {
            var content = "public class Main { \n" +
                    "   public static void main(String[] args) {\n" +
                    "      boolean test = true;\n" +
                    "      while(test) {\n" +
                    "        System.out.println(\"toto\");\n" +
                    "        test = !test;\n" +
                    "      }\n" +
                    "   }\n" +
                    "}";
            assertThat(sut.getCyclomaticComplexity(content)).isEqualTo(1L);
        }

        @Test
        void when_one_while_and_nested_if_should_return_2() {
            var content = "public class Main { \n" +
                    "   public static void main(String[] args) {\n" +
                    "      boolean test = true;\n" +
                    "      while(test) {\n" +
                    "        test = !test;\n" +
                    "        if (test == false) {\n" +
                    "            System.out.println(\"toto\");\n" +
                    "        }\n" +
                    "      }\n" +
                    "   }\n" +
                    "}";
            assertThat(sut.getCyclomaticComplexity(content)).isEqualTo(2L);
        }

        @Test
        void when_one_forEach_should_return_1() {
            var content = "public class Main { \n" +
                    "   public static void main(String[] args) {\n" +
                    "      List<String> strList = List.of(\"toto\", \"tata\");\n" +
                    "      \n" +
                    "      strList.forEach(curStr -> {\n" +
                    "        System.out.println(curStr);\n" +
                    "      });\n" +
                    "   }\n" +
                    "}";
            assertThat(sut.getCyclomaticComplexity(content)).isEqualTo(1L);
        }
    }
}