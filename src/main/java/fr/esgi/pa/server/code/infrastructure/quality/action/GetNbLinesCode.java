package fr.esgi.pa.server.code.infrastructure.quality.action;

import fr.esgi.pa.server.code.core.quality.QualityCode;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByLanguage;
import org.springframework.stereotype.Component;

@Component
public class GetNbLinesCode implements QualityCodeAction {
    @Override
    public QualityCode execute(ActionsByLanguage actionsByLanguage, QualityCode currentQualityCode) {
        var resultNbLinesCode = actionsByLanguage.getNbLinesCode(currentQualityCode.getCodeContent());
        currentQualityCode.setLinesCode(resultNbLinesCode);
        return currentQualityCode;
    }
}
