package fr.esgi.pa.server.unit.code.infrastructure.quality.action;

import fr.esgi.pa.server.code.core.quality.QualityCode;
import fr.esgi.pa.server.code.infrastructure.quality.action.GetCyclomaticComplexity;
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
class GetCyclomaticComplexityTest {
    private GetCyclomaticComplexity sut;

    @Mock
    private ActionsByLanguage mockActionsByLanguage;

    @BeforeEach
    void setup() {
        sut = new GetCyclomaticComplexity();
    }

    @Test
    void should_call_actionsByLanguage_getCyclomaticComplexity_method() {
        var qualityCode = new QualityCode()
                .setLanguage(new Language().setId(1L).setLanguageName(LanguageName.C11).setFileExtension("c"))
                .setCodeContent("code content");
        sut.execute(mockActionsByLanguage, qualityCode);

        verify(mockActionsByLanguage, times(1)).getCyclomaticComplexity(qualityCode.getCodeContent());
    }

    @Test
    void when_actionsByLanguage_return_cyclomatic_complexity_should_set_to_quality_code_and_return() {
        var qualityCode = new QualityCode()
                .setLanguage(new Language().setId(1L).setLanguageName(LanguageName.C11).setFileExtension("c"))
                .setCodeContent("code content");
        when(mockActionsByLanguage.getCyclomaticComplexity(qualityCode.getCodeContent())).thenReturn(4L);

        var result = sut.execute(mockActionsByLanguage, qualityCode);

        assertThat(result).isNotNull();
        assertThat(result.getCyclomaticComplexity()).isEqualTo(4L);
    }
}