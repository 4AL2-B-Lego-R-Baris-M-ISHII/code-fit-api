package fr.esgi.pa.server.integration.code.infrastructure.quality.factory;

import fr.esgi.pa.server.code.core.dto.CodeQualityType;
import fr.esgi.pa.server.code.infrastructure.quality.action.GetCyclomaticComplexity;
import fr.esgi.pa.server.code.infrastructure.quality.action.GetNbLinesCode;
import fr.esgi.pa.server.code.infrastructure.quality.action.GetNbLinesComment;
import fr.esgi.pa.server.code.infrastructure.quality.factory.QualityCodeActionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
class QualityCodeActionFactoryTest {
    @Autowired
    private QualityCodeActionFactory sut;

    @Test
    void when_code_quality_type_is_lines_code_should_return_GetLinesCode_instance() {
        var result = sut.getAction(CodeQualityType.LINES_CODE);

        assertThat(result).isNotNull();
        assertThat(result).isExactlyInstanceOf(GetNbLinesCode.class);
    }

    @Test
    void when_code_quality_type_is_lines_comment_should_return_GetLinesComment_instance() {
        var result = sut.getAction(CodeQualityType.LINES_COMMENT);

        assertThat(result).isNotNull();
        assertThat(result).isExactlyInstanceOf(GetNbLinesComment.class);
    }

    @Test
    void when_code_quality_is_cyclomatic_complexity_should_return_GetCyclomaticComplexity_instance() {
        var result = sut.getAction(CodeQualityType.CYCLOMATIC_COMPLEXITY);

        assertThat(result).isNotNull();
        assertThat(result).isExactlyInstanceOf(GetCyclomaticComplexity.class);
    }
}