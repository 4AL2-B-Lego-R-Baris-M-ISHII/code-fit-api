package fr.esgi.pa.server.code.infrastructure.quality.action;

import fr.esgi.pa.server.code.core.quality.QualityCode;

public interface QualityCodeAction {
    QualityCode execute(QualityCode currentQualityCode);
}
