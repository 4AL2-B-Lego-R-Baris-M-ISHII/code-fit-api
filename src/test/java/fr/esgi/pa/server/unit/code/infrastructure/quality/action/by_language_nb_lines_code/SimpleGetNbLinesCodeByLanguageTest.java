package fr.esgi.pa.server.unit.code.infrastructure.quality.action.by_language_nb_lines_code;

import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.nb_lines_code.SimpleGetNbLinesCodeByLanguage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SimpleGetNbLinesCodeByLanguageTest {
    private SimpleGetNbLinesCodeByLanguage sut;

    @BeforeEach
    void setup() {
        sut = new SimpleGetNbLinesCodeByLanguage();
    }

    @Test
    void when_code_contain_one_lines_should_return_1() {
        var codeContent = "int main() {return 0;}";

        var result = sut.execute(codeContent);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void when_code_contain_one_line_break_linux_version_should_return_2() {
        var codeContent = "int main() {\nreturn 0;}";


        var result = sut.execute(codeContent);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(2L);
    }

    @Test
    void when_code_contain_one_line_break_mac_version_version_should_return_2() {
        var codeContent = "int main() {\rreturn 0;}";

        var result = sut.execute(codeContent);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(2L);
    }

    @Test
    void when_code_contains_one_line_break_windows_version_should_return_2() {
        var codeContent = "int main() {\r\nreturn 0;}";

        var result = sut.execute(codeContent);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(2L);
    }

    @Test
    void when_code_contains_5_lines_break_linux_version_should_return_6() {
        var codeContent = "int main() {\n\n\n\nreturn 0;\n}";

        var result = sut.execute(codeContent);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(6L);
    }

    @Test
    void when_code_contains_3_lines_break_max_version_should_return_4() {
        var codeContent = "#include <stdio.h>\rint main() {\rint test = 1;\rreturn 0;}";

        var result = sut.execute(codeContent);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(4L);
    }
}