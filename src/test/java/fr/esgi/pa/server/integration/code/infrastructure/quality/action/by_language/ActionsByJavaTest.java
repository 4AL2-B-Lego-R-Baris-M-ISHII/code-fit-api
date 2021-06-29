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
    class GetNbLinesComment {
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
}