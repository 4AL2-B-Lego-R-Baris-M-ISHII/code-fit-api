package fr.esgi.pa.server.unit.code.infrastructure.quality.action;

import fr.esgi.pa.server.code.core.quality.QualityCode;
import fr.esgi.pa.server.code.infrastructure.quality.action.GetNbLinesCode;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByLanguage;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetNbLinesCodeTest {
    private final long languageId = 1L;
    private GetNbLinesCode sut;

    @Mock
    private ActionsByLanguage mockActionsByLanguage;
    private final String codeContent = "code content";
    ;

    @BeforeEach
    void setup() {
        sut = new GetNbLinesCode();
    }

    @Test
    void should_call_actionsByLanguage_getNbLinesCode_of_qualityCode_content() {
        var qualityCode = new QualityCode()
                .setCodeContent(codeContent)
                .setLanguage(new Language().setId(languageId).setFileExtension("c").setLanguageName(LanguageName.C11));


        sut.execute(mockActionsByLanguage, qualityCode);

        verify(mockActionsByLanguage, times(1)).getNbLinesCode(codeContent);
    }

    @Test
    void when_get_result_of_actionsByLanguage_should_set_result_to_current_quality_code_and_return() {
        var language = new Language().setId(languageId).setFileExtension("c").setLanguageName(LanguageName.C11);
        var qualityCode = new QualityCode()
                .setCodeContent(codeContent)
                .setLanguage(language);
        var expectedNbLinesCode = 7L;
        when(mockActionsByLanguage.getNbLinesCode(codeContent)).thenReturn(expectedNbLinesCode);

        var result = sut.execute(mockActionsByLanguage, qualityCode);

        var expectedQualityCode = new QualityCode()
                .setLinesCode(expectedNbLinesCode)
                .setCodeContent(codeContent)
                .setLanguage(language);
        assertThat(result).isEqualTo(expectedQualityCode);
    }
}