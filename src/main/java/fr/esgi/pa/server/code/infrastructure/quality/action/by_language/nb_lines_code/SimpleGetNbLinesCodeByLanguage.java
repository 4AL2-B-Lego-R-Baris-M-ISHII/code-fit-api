package fr.esgi.pa.server.code.infrastructure.quality.action.by_language.nb_lines_code;

import org.springframework.stereotype.Component;

@Component
public class SimpleGetNbLinesCodeByLanguage implements GetNbLinesCodeByLanguage {
    @Override
    public Long execute(String content) {
        var splitCodeContent = content.split("\\r\\n|\\r|\\n");
        return (long) splitCodeContent.length;
    }
}
