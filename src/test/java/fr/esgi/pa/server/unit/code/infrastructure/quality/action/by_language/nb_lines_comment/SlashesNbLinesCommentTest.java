package fr.esgi.pa.server.unit.code.infrastructure.quality.action.by_language.nb_lines_comment;

import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.nb_lines_comment.SlashesNbLinesComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SlashesNbLinesCommentTest {
    private SlashesNbLinesComment sut;

    @BeforeEach
    void setup() {
        sut = new SlashesNbLinesComment();
    }

    @Test
    void when_code_not_contains_double_slash_should_return_0() {
        var content = "int main() {\n" +
                "int test = 2;\n" +
                "test = 0;\n" +
                "return test;" +
                "}\n";
        assertThat(sut.execute(content)).isEqualTo(0L);
    }

    @Test
    void when_code_contain_one_line_start_with_double_slash_should_return_1() {
        var content = "// comment one line";
        assertThat(sut.execute(content)).isEqualTo(1L);
    }

    @Test
    void when_code_contain_one_line_not_start_but_contain_double_slash_should_return_1() {
        var content = "int main() {\n" +
                "int test = 2; // variable test\n" +
                "test = 0;\n" +
                "return test;" +
                "}\n";

        assertThat(sut.execute(content)).isEqualTo(1L);
    }

    @Test
    void when_code_contain_3_lines_with_double_slash_should_return_3() {
        var content = "int main() {\n" +
                "int test = 2; // variable test\n" +
                "// test equal zero\n" +
                "test = 0;\n" +
                "// return variable" +
                "return test;" +
                "}\n";

        assertThat(sut.execute(content)).isEqualTo(3L);
    }

    @Test
    void when_code_contain_one_line_with_slash_asterisk_comments_and_finish_parts_should_return_1() {
        var content = "/* comment one line */";
        assertThat(sut.execute(content)).isEqualTo(1L);
    }

    @Test
    void when_code_contain_4_line_and_one_line_slash_asterisk_comment_should_return_1() {
        var content = "int main() { \n" +
                "/*middle*/\n" +
                "return 0;" +
                "}\n";
        assertThat(sut.execute(content)).isEqualTo(1L);
    }

    @Test
    void when_code_contain_three_line_with_one_slash_asterisk_comments_and_finish_parts_should_return_3() {
        var content = "/* comment two line \n" +
                "middle\n" +
                "and finish comment  *\n";
        assertThat(sut.execute(content)).isEqualTo(3L);
    }

    @Test
    void when_code_contain_two_line_with_one_slash_asterisk_comments_and_finish_parts_and_one_double_slash_should_return_3() {
        var content = "/* comment two line \n" +
                "and finish comment  */\n" +
                "int main() {return0;}\n" +
                "//finish";
        assertThat(sut.execute(content)).isEqualTo(3L);
    }

    @Test
    void when_code_contain_3_line_with_one_line_double_slash_and_in_same_line_slash_asterisk_comment_with_end_part_should_return_1() {
        var content = "// comment /*line*/ \n" +
                "int main() { return 0;\n" +
                "}\n";
        assertThat(sut.execute(content)).isEqualTo(1L);
    }

    @Test
    void when_code_contain_3_line_with_start_one_line_double_slash_and_in_same_line_slash_asterisk_comment_without_end_part_should_return_1() {
        var content = "// comment /*line \n" +
                "int main() { return 0;\n" +
                "}\n";
        assertThat(sut.execute(content)).isEqualTo(1L);
    }

    @Test
    void when_code_contain_5_line_with_start_3_lines_slash_asterisk_comment_that_contain_double_slash_comment_on_2nd_line_should_return_3() {
        var content = "int main() {\n" +
                "/* comment \n" +
                "// test\n" +
                "end */\n" +
                "return 0;\n" +
                "}\n";
        assertThat(sut.execute(content)).isEqualTo(3L);
    }

    @Test
    void when_code_contain_3_line_with_start_one_line_double_slash_and_in_same_line_slash_asterisk_comment_without_end_part_should_return_2() {
        var content = "// comment /*line \n" +
                "int main() { return 0;\n" +
                "}\n";
        assertThat(sut.execute(content)).isEqualTo(1L);
    }

    @Test
    void given_code_contain_5_lines_inside_3_lines_slash_asterisk_comment_inside_one_double_slash_comment_should_return_3() {
        var content = "/* comment line // with double slash comment\n" +
                "int main() { return 0;\n" +
                "}*/\n" +
                "int main() {return 0;}";
        assertThat(sut.execute(content)).isEqualTo(3L);
    }
}