package fr.esgi.pa.server.code.infrastructure.quality.factory;

import fr.esgi.pa.server.code.core.dto.CodeQualityType;
import fr.esgi.pa.server.code.infrastructure.quality.action.GetNbLinesCode;
import fr.esgi.pa.server.code.infrastructure.quality.action.GetNbLinesComment;
import fr.esgi.pa.server.code.infrastructure.quality.action.QualityCodeAction;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QualityCodeActionFactory {
    private final ApplicationContext applicationContext;
    public QualityCodeAction getAction(CodeQualityType codeQualityType) {
        if (codeQualityType.equals(CodeQualityType.LINES_CODE)) {
            return applicationContext.getBean(GetNbLinesCode.class);
        }
        return applicationContext.getBean(GetNbLinesComment.class);
    }
}
