package fr.esgi.pa.server.code.infrastructure.quality.factory;

import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByC;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByJava;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByLanguage;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import org.springframework.stereotype.Component;

@Component
public class ActionsByLanguageFactory {
    public ActionsByLanguage getActionsByLanguage(Language language) {
        if (language.getLanguageName().equals(LanguageName.C)) {
            return new ActionsByC();
        }
        return new ActionsByJava();
    }
}
