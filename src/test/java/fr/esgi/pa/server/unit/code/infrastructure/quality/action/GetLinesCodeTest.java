package fr.esgi.pa.server.unit.code.infrastructure.quality.action;

import fr.esgi.pa.server.code.core.quality.QualityCode;
import fr.esgi.pa.server.code.infrastructure.quality.action.GetLinesCode;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GetLinesCodeTest {
    private final long languageId = 48L;
    private GetLinesCode sut;

    @BeforeEach
    void setup() {
        sut = new GetLinesCode();
    }

    @Test
    void when_code_contain_one_lines_should_affect_currentQualityCode_lines_code_property_to_1() {
        var language = new Language()
                .setId(languageId)
                .setFileExtension("c")
                .setLanguageName(LanguageName.C);
        var codeContent = "int main() {return 0;}";
        var qualityCode = new QualityCode()
                .setLanguage(language)
                .setCodeContent(codeContent);

        var result = sut.execute(qualityCode);

        assertThat(result).isNotNull();
        assertThat(result.getLinesCode()).isEqualTo(1L);
    }

    @Test
    void when_code_contain_one_line_break_linux_version_should_set_currentQualityCode_lines_code_property_to_2_and_return_result() {
        var language = new Language()
                .setId(languageId)
                .setFileExtension("c")
                .setLanguageName(LanguageName.C);
        var codeContent = "int main() {\nreturn 0;}";
        var qualityCode = new QualityCode()
                .setLanguage(language)
                .setCodeContent(codeContent);

        var result = sut.execute(qualityCode);

        assertThat(result).isNotNull();
        assertThat(result.getLinesCode()).isEqualTo(2L);
    }

    @Test
    void when_code_contain_one_line_break_mac_version_version_should_set_currentQualityCode_lines_code_property_to_2_and_return_result() {
        var language = new Language()
                .setId(languageId)
                .setFileExtension("c")
                .setLanguageName(LanguageName.C);
        var codeContent = "int main() {\rreturn 0;}";
        var qualityCode = new QualityCode()
                .setLanguage(language)
                .setCodeContent(codeContent);

        var result = sut.execute(qualityCode);

        assertThat(result).isNotNull();
        assertThat(result.getLinesCode()).isEqualTo(2L);
    }

    @Test
    void when_code_contains_one_line_break_windows_version_should_set_currentQualityCode_lines_code_property_to_2_and_return_result() {
        var language = new Language()
                .setId(languageId)
                .setFileExtension("c")
                .setLanguageName(LanguageName.C);
        var codeContent = "int main() {\r\nreturn 0;}";
        var qualityCode = new QualityCode()
                .setLanguage(language)
                .setCodeContent(codeContent);

        var result = sut.execute(qualityCode);

        assertThat(result).isNotNull();
        assertThat(result.getLinesCode()).isEqualTo(2L);
    }

    @Test
    void when_code_contains_5_lines_break_linux_version_should_return_currentQualityCode_lines_code_property_to_6() {
        var language = new Language()
                .setId(languageId)
                .setFileExtension("c")
                .setLanguageName(LanguageName.C);
        var codeContent = "int main() {\n\n\n\nreturn 0;\n}";
        var qualityCode = new QualityCode()
                .setLanguage(language)
                .setCodeContent(codeContent);

        var result = sut.execute(qualityCode);

        assertThat(result).isNotNull();
        assertThat(result.getLinesCode()).isEqualTo(6L);
    }

    @Test
    void when_code_contains_3_lines_break_max_version_should_return_currentQualityCode_lines_code_property_to_4() {
        var language = new Language()
                .setId(languageId)
                .setFileExtension("c")
                .setLanguageName(LanguageName.C);
        var codeContent = "#include <stdio.h>\rint main() {\rint test = 1;\rreturn 0;}";
        var qualityCode = new QualityCode()
                .setLanguage(language)
                .setCodeContent(codeContent);

        var result = sut.execute(qualityCode);

        assertThat(result).isNotNull();
        assertThat(result.getLinesCode()).isEqualTo(4L);
    }
}