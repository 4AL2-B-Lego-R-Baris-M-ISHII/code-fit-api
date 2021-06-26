package fr.esgi.pa.server.code.infrastructure.quality.action.by_language;

import org.springframework.stereotype.Component;

@Component
public class ActionsByJava implements ActionsByLanguage {
    @Override
    public Long getNbLinesCode(String content) {
        return null;
    }

    @Override
    public Long getNbLinesComment(String content) {
        return null;
    }
}
