package fr.esgi.pa.server.code.infrastructure.quality.action;

import fr.esgi.pa.server.code.core.quality.QualityCode;
import org.springframework.stereotype.Component;

@Component
public class GetLinesCode implements QualityCodeAction {
    @Override
    public QualityCode execute(QualityCode currentQualityCode) {
        var splitCodeContent = currentQualityCode.getCodeContent().split("\\r\\n|\\r|\\n");

        currentQualityCode.setLinesCode((long) splitCodeContent.length);
        return currentQualityCode;
    }
}
