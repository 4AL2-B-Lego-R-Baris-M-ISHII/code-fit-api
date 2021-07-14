package fr.esgi.pa.server.code.core.quality;

import fr.esgi.pa.server.code.core.dto.CodeQualityType;
import fr.esgi.pa.server.language.core.Language;

import java.util.Set;

public interface ProcessQualityCode {
    QualityCode process(String content, Language language, Set<CodeQualityType> qualityTypeStack);
}
