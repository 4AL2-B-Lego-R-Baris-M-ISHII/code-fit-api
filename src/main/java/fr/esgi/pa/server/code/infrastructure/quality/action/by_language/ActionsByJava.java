package fr.esgi.pa.server.code.infrastructure.quality.action.by_language;

import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.nb_lines_code.GetNbLinesCodeByLanguage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActionsByJava implements ActionsByLanguage {
    private final GetNbLinesCodeByLanguage getNbLinesCodeByLanguage;
    
    @Override
    public Long getNbLinesCode(String content) {
        return getNbLinesCodeByLanguage.execute(content);
    }

    @Override
    public Long getNbLinesComment(String content) {
        return null;
    }
}
