package fr.esgi.pa.server.code.infrastructure.quality.action;

import fr.esgi.pa.server.code.core.quality.QualityCode;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByLanguage;
import org.springframework.stereotype.Component;

@Component
public class HasDuplicateCode implements QualityCodeAction {
    @Override
    public QualityCode execute(ActionsByLanguage actionsByLanguage, QualityCode currentQualityCode) {
        var hasRedundantCode = actionsByLanguage.hasDuplicateCode(currentQualityCode.getCodeContent());
        currentQualityCode.setHasDuplicateCode(hasRedundantCode);

        return currentQualityCode;
    }
}
