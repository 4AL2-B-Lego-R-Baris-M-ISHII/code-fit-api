package fr.esgi.pa.server.language.infrastructure.bootstrap;

import fr.esgi.pa.server.common.core.exception.AlreadyCreatedException;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.log.core.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LanguageBootstrap {
    private final LanguageDao languageDao;
    private final Log<LanguageBootstrap> logger;

    @EventListener
    public void on(ApplicationReadyEvent event) {
        var mapLanguagesValues = Map.of(
                LanguageName.C11, "c",
                LanguageName.JAVA8, "java"
        );
        mapLanguagesValues.forEach((languageName, extFile) -> {
            try {
                languageDao.createLanguage(languageName, extFile);
            } catch (AlreadyCreatedException e) {
                var message = String.format(
                        "Language with name '%s' not save because already created",
                        languageName
                );
                logger.info(LanguageBootstrap.class, message);
            }
        });
    }
}
