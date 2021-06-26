package fr.esgi.pa.server.code.infrastructure.quality.factory;

import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByC;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByJava;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByLanguage;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActionsByLanguageFactory {
    private final ApplicationContext applicationContext;
    public ActionsByLanguage getActionsByLanguage(Language language) {
        if (language.getLanguageName().equals(LanguageName.JAVA)) {
            return applicationContext.getBean(ActionsByJava.class);
        }
        return applicationContext.getBean(ActionsByC.class);
    }
}
