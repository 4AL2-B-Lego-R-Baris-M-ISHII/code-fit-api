package fr.esgi.pa.server.code.infrastructure.quality.action;

import fr.esgi.pa.server.code.core.quality.QualityCode;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByLanguage;

public interface QualityCodeAction {
    QualityCode execute(ActionsByLanguage actionsByLanguage, QualityCode currentQualityCode);
}
