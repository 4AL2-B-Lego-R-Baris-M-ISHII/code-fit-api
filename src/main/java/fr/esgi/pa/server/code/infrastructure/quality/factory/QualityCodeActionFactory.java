package fr.esgi.pa.server.code.infrastructure.quality.factory;

import fr.esgi.pa.server.code.core.dto.CodeQualityType;
import fr.esgi.pa.server.code.infrastructure.quality.action.GetCyclomaticComplexity;
import fr.esgi.pa.server.code.infrastructure.quality.action.GetNbLinesCode;
import fr.esgi.pa.server.code.infrastructure.quality.action.GetNbLinesComment;
import fr.esgi.pa.server.code.infrastructure.quality.action.QualityCodeAction;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Hashtable;
import java.util.Map;

@Component
public class QualityCodeActionFactory {
    private final Map<CodeQualityType, QualityCodeAction> mapCodeQualityTypeAndAction;

    public QualityCodeActionFactory(ApplicationContext applicationContext) {
        mapCodeQualityTypeAndAction = new Hashtable<>();
        mapCodeQualityTypeAndAction.put(CodeQualityType.LINES_CODE, applicationContext.getBean(GetNbLinesCode.class));
        mapCodeQualityTypeAndAction.put(CodeQualityType.CYCLOMATIC_COMPLEXITY, applicationContext.getBean(GetCyclomaticComplexity.class));
        mapCodeQualityTypeAndAction.put(CodeQualityType.LINES_COMMENT, applicationContext.getBean(GetNbLinesComment.class));
    }

    public QualityCodeAction getAction(CodeQualityType codeQualityType) {
        return mapCodeQualityTypeAndAction.get(codeQualityType);
    }
}
