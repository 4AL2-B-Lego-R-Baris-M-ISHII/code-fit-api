package fr.esgi.pa.server.unit.code.infrastructure.quality;

import fr.esgi.pa.server.code.core.dto.CodeQualityType;
import fr.esgi.pa.server.code.core.quality.QualityCode;
import fr.esgi.pa.server.code.infrastructure.quality.ProcessQualityCodeImpl;
import fr.esgi.pa.server.code.infrastructure.quality.action.QualityCodeAction;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByLanguage;
import fr.esgi.pa.server.code.infrastructure.quality.factory.ActionsByLanguageFactory;
import fr.esgi.pa.server.code.infrastructure.quality.factory.QualityCodeActionFactory;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessQualityCodeImplTest {
    private final String codeContent = "code content";
    private final long languageId = 163L;
    private final Language language = new Language()
            .setId(languageId)
            .setFileExtension("c")
            .setLanguageName(LanguageName.C11);
    private Set<CodeQualityType> codeQualityTypeSet;

    private ProcessQualityCodeImpl sut;

    @Mock
    private QualityCodeActionFactory mockQualityCodeActionFactory;

    @Mock
    private ActionsByLanguageFactory mockActionsByLanguageFactory;

    @Mock
    private QualityCodeAction mockQualityCodeAction;

    @Mock
    private ActionsByLanguage mockActionsByLanguage;

    @BeforeEach
    void setup() {
        sut = new ProcessQualityCodeImpl(mockQualityCodeActionFactory, mockActionsByLanguageFactory);

    }

    @Test
    void when_get_action_by_factory_should_call_action_command() {
        when(mockActionsByLanguageFactory.getActionsByLanguage(language)).thenReturn(mockActionsByLanguage);
        codeQualityTypeSet = Set.of(CodeQualityType.LINES_CODE);
        when(mockQualityCodeActionFactory.getAction(CodeQualityType.LINES_CODE)).thenReturn(mockQualityCodeAction);

        sut.process(codeContent, language, codeQualityTypeSet);

        var expectedQualityCode = new QualityCode()
                .setCodeContent(codeContent)
                .setLanguage(language);
        verify(mockQualityCodeAction, times(1)).execute(mockActionsByLanguage, expectedQualityCode);
    }

    @Test
    void when_only_one_set_of_code_quality_type_should_update_one_times_and_return_quality_code() {
        when(mockActionsByLanguageFactory.getActionsByLanguage(language)).thenReturn(mockActionsByLanguage);
        codeQualityTypeSet = Set.of(CodeQualityType.LINES_CODE);
        when(mockQualityCodeActionFactory.getAction(CodeQualityType.LINES_CODE)).thenReturn(mockQualityCodeAction);
        var qualityCode = new QualityCode()
                .setCodeContent(codeContent)
                .setLanguage(language);
        var updatedQualityCode = new QualityCode()
                .setCodeContent(codeContent)
                .setLanguage(language)
                .setLinesCode(10L);
        when(mockQualityCodeAction.execute(mockActionsByLanguage, qualityCode)).thenReturn(updatedQualityCode);

        var result = sut.process(codeContent, language, codeQualityTypeSet);

        assertThat(result).isEqualTo(updatedQualityCode);
    }
}