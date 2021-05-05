package fr.esgi.pa.server.language.infrastructure.bootstrap;

import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.log.core.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LanguageBootstrap {
    //private final LanguageDao languageDao;
    private final Log<LanguageBootstrap> logger;

    @EventListener
    public void on(ApplicationReadyEvent event) {

    }
}
