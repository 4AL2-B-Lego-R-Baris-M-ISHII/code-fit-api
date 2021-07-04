package fr.esgi.pa.server.code.infrastructure.quality.action;

import fr.esgi.pa.server.code.core.quality.QualityCode;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByLanguage;
import org.springframework.stereotype.Component;

@Component
public class GetCyclomaticComplexity implements QualityCodeAction {
    @Override
    public QualityCode execute(ActionsByLanguage actionsByLanguage, QualityCode currentQualityCode) {
        var result = actionsByLanguage.getCyclomaticComplexity(currentQualityCode.getCodeContent());
        currentQualityCode.setCyclomaticComplexity(result);

        return currentQualityCode;
    }
}
