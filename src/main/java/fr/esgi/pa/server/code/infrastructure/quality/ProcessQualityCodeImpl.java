package fr.esgi.pa.server.code.infrastructure.quality;

import fr.esgi.pa.server.code.core.dto.CodeQualityType;
import fr.esgi.pa.server.code.core.quality.ProcessQualityCode;
import fr.esgi.pa.server.code.core.quality.QualityCode;
import fr.esgi.pa.server.language.core.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ProcessQualityCodeImpl implements ProcessQualityCode {
    private final QualityCodeActionFactory factory;

    @Override
    public QualityCode process(String content, Language language, Set<CodeQualityType> codeQualityTypeSet) {
        var qualityCode = new QualityCode()
                .setCodeContent(content)
                .setLanguage(language);
        for (CodeQualityType type : codeQualityTypeSet) {
            var action = factory.getAction(type);
            qualityCode = action.execute(qualityCode);
        }
        return qualityCode;
    }
}
