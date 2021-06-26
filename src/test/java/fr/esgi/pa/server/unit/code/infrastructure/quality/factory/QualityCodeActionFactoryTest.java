package fr.esgi.pa.server.unit.code.infrastructure.quality.factory;

import fr.esgi.pa.server.code.core.dto.CodeQualityType;
import fr.esgi.pa.server.code.infrastructure.quality.action.GetNbLinesCode;
import fr.esgi.pa.server.code.infrastructure.quality.factory.QualityCodeActionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class QualityCodeActionFactoryTest {
    private QualityCodeActionFactory sut;

    @BeforeEach
    void setup() {
        sut = new QualityCodeActionFactory();
    }

    @Test
    void when_code_quality_type_is_lines_code_should_return_GetLinesCode_instance() {
        var result = sut.getAction(CodeQualityType.LINES_CODE);

        assertThat(result).isNotNull();
        assertThat(result).isExactlyInstanceOf(GetNbLinesCode.class);
    }
}