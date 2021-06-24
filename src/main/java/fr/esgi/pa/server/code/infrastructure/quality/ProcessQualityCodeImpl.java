package fr.esgi.pa.server.code.infrastructure.quality;

import fr.esgi.pa.server.code.core.dto.CodeQualityType;
import fr.esgi.pa.server.code.core.quality.ProcessQualityCode;
import fr.esgi.pa.server.code.core.quality.QualityCode;
import fr.esgi.pa.server.language.core.Language;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ProcessQualityCodeImpl implements ProcessQualityCode {
    @Override
    public QualityCode process(String content, Language language, Set<CodeQualityType> qualityTypeStack) {
        return null;
    }
}
