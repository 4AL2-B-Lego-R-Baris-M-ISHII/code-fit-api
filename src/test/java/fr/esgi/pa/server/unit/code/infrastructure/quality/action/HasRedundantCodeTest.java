package fr.esgi.pa.server.unit.code.infrastructure.quality.action;

import fr.esgi.pa.server.code.core.quality.QualityCode;
import fr.esgi.pa.server.code.infrastructure.quality.action.HasRedundantCode;
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
class HasRedundantCodeTest {
    private HasRedundantCode sut;

    @Mock
    private ActionsByLanguage mockActionsByLanguage;

    @BeforeEach
    void setup() {
        sut = new HasRedundantCode();
    }

    @Test
    void should_call_actions_by_language_with_code_content() {
        var qualityCode = new QualityCode()
                .setLanguage(new Language().setId(1L).setLanguageName(LanguageName.C11).setFileExtension("c"))
                .setCodeContent("code content");
        sut.execute(mockActionsByLanguage, qualityCode);

        verify(mockActionsByLanguage, times(1)).hasRedundantCode(qualityCode.getCodeContent());
    }

    @Test
    void when_call_actions_by_language_hasRedundantCode_return_true_should_set_quality_code_hasRedundantCode_property_to_true() {
        var qualityCode = new QualityCode()
                .setLanguage(new Language().setId(1L).setLanguageName(LanguageName.C11).setFileExtension("c"))
                .setCodeContent("code content");
        when(mockActionsByLanguage.hasRedundantCode(qualityCode.getCodeContent())).thenReturn(true);

        var result = sut.execute(mockActionsByLanguage, qualityCode);

        assertThat(result).isNotNull();
        assertThat(result.getHasRedundantCode()).isTrue();
    }

    @Test
    void when_call_actions_by_language_hasRedundantCode_return_false_should_set_quality_code_hasRedundantCode_property_to_false() {
        var qualityCode = new QualityCode()
                .setLanguage(new Language().setId(1L).setLanguageName(LanguageName.C11).setFileExtension("c"))
                .setCodeContent("code content");
        when(mockActionsByLanguage.hasRedundantCode(qualityCode.getCodeContent())).thenReturn(false);

        var result = sut.execute(mockActionsByLanguage, qualityCode);

        assertThat(result).isNotNull();
        assertThat(result.getHasRedundantCode()).isFalse();
    }
}