package fr.esgi.pa.server.unit.code.infrastructure.quality;

import fr.esgi.pa.server.code.core.dto.CodeQualityType;
import fr.esgi.pa.server.code.infrastructure.quality.ProcessQualityCodeImpl;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class ProcessQualityCodeImplTest {
    private final String codeContent = "code content";
    private final long languageId = 163L;
    private final Language language = new Language()
            .setId(languageId)
            .setFileExtension("c")
            .setLanguageName(LanguageName.C);
    private Set<CodeQualityType> codeQualityTypeSet;

    private ProcessQualityCodeImpl sut;

    @BeforeEach
    void setup() {
        sut = new ProcessQualityCodeImpl();

    }

    @Test
    void when_qualityTypeStack_contain_one_code_quality_type_should_call_factory_to_get_concerned_action_code_quality() {
        codeQualityTypeSet = Set.of(CodeQualityType.LINES_CODE);

        sut.process(codeContent, language, codeQualityTypeSet);
    }
}