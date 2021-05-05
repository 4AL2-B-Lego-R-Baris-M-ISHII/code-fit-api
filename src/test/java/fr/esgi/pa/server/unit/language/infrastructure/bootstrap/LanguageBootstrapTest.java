package fr.esgi.pa.server.unit.language.infrastructure.bootstrap;

import fr.esgi.pa.server.common.exception.AlreadyCreatedException;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.language.infrastructure.bootstrap.LanguageBootstrap;
import fr.esgi.pa.server.log.core.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LanguageBootstrapTest {
    @Mock
    private ApplicationReadyEvent mockApplicationReadyEvent;

    @Mock
    private LanguageDao mockLanguageDao;

    @Mock
    private Log<LanguageBootstrap> mockLogger;

    private LanguageBootstrap sut;

    @BeforeEach
    void setup() {
        sut = new LanguageBootstrap(mockLanguageDao, mockLogger);
    }

    @Test
    void when_list_languages_are_empty_should_save_all_languages() throws AlreadyCreatedException {
        sut.on(mockApplicationReadyEvent);
        verify(mockLanguageDao, times(1)).createLanguage(LanguageName.C, "c");
        verify(mockLanguageDao, times(1)).createLanguage(LanguageName.CPP, "cpp");
        verify(mockLanguageDao, times(1)).createLanguage(LanguageName.JAVA, "java");
        verify(mockLanguageDao, times(1)).createLanguage(LanguageName.RUST, "rs");
        verify(mockLanguageDao, times(1)).createLanguage(LanguageName.TYPESCRIPT, "ts");
    }

    @Test
    void when_language_already_created_should_log() throws AlreadyCreatedException {
        when(mockLanguageDao.createLanguage(LanguageName.C, "c")).thenThrow(new AlreadyCreatedException("c already created"));
        when(mockLanguageDao.createLanguage(LanguageName.CPP, "cpp")).thenReturn(2L);
        when(mockLanguageDao.createLanguage(LanguageName.JAVA, "java")).thenReturn(3L);
        when(mockLanguageDao.createLanguage(LanguageName.RUST, "rs")).thenReturn(4L);
        when(mockLanguageDao.createLanguage(LanguageName.TYPESCRIPT, "ts")).thenReturn(5L);

        sut.on(mockApplicationReadyEvent);

        var message = String.format("Language with name '%s' not save because already created", LanguageName.C);
        verify(mockLogger, times(1)).info(LanguageBootstrap.class, message);
    }
}