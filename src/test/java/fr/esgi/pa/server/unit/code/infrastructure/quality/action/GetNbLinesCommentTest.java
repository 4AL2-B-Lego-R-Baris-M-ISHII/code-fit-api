package fr.esgi.pa.server.unit.code.infrastructure.quality.action;

import fr.esgi.pa.server.code.core.quality.QualityCode;
import fr.esgi.pa.server.code.infrastructure.quality.action.GetNbLinesComment;
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
class GetNbLinesCommentTest {
    private final String contentCode = "content code";
    private GetNbLinesComment sut;

    @Mock
    private ActionsByLanguage mockActionsByLanguage;
    private final long languageId = 164L;
    private final Language language = new Language().setId(languageId).setFileExtension("c").setLanguageName(LanguageName.C11);

    @BeforeEach
    void setup() {
        sut = new GetNbLinesComment();
    }

    @Test
    void should_call_actionsByLanguage_get_nb_lines_comment() {
        var qualityCode = new QualityCode()
                .setCodeContent(contentCode)
                .setLanguage(language);
        sut.execute(mockActionsByLanguage, qualityCode);

        verify(mockActionsByLanguage, times(1)).getNbLinesComment(contentCode);
    }

    @Test
    void when_get_result_of_actionsByLanguage_getNbLinesComment_should_set_quality_code_linesComment_and_return_quality_code() {
        var qualityCode = new QualityCode()
                .setCodeContent(contentCode)
                .setLanguage(language);
        assertThat(qualityCode.getNbLinesComment()).isNull();
        when(mockActionsByLanguage.getNbLinesComment(contentCode)).thenReturn(7L);

        var result = sut.execute(mockActionsByLanguage, qualityCode);

        var expectedQualityCode = new QualityCode()
                .setCodeContent(contentCode)
                .setLanguage(language)
                .setNbLinesComment(7L);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedQualityCode);
    }
}
