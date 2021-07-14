package fr.esgi.pa.server.code.infrastructure.quality.action;

import fr.esgi.pa.server.code.core.quality.QualityCode;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByLanguage;
import org.springframework.stereotype.Component;

@Component
public class GetNbLinesComment implements QualityCodeAction {
    @Override
    public QualityCode execute(ActionsByLanguage actionsByLanguage, QualityCode currentQualityCode) {
        var resultNbLinesComment = actionsByLanguage.getNbLinesComment(currentQualityCode.getCodeContent());
        currentQualityCode.setNbLinesComment(resultNbLinesComment);
        return currentQualityCode;
    }
}
