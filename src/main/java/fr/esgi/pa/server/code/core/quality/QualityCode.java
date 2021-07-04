package fr.esgi.pa.server.code.core.quality;

import fr.esgi.pa.server.language.core.Language;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class QualityCode {
    private String codeContent;
    private Language language;
    private Long nbLinesCode;
    private Long nbLinesComment;
    private Long cyclomaticComplexity;
}
