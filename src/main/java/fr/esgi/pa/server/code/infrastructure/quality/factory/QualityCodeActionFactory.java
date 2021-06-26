package fr.esgi.pa.server.code.infrastructure.quality.factory;

import fr.esgi.pa.server.code.core.dto.CodeQualityType;
import fr.esgi.pa.server.code.infrastructure.quality.action.GetNbLinesCode;
import fr.esgi.pa.server.code.infrastructure.quality.action.QualityCodeAction;
import org.springframework.stereotype.Component;

@Component
public class QualityCodeActionFactory {

    public QualityCodeAction getAction(CodeQualityType codeQualityType) {
        if (codeQualityType.equals(CodeQualityType.LINES_CODE)) {
            return new GetNbLinesCode();
        }
        return null;
    }
}
