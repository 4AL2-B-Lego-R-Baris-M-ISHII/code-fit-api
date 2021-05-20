package fr.esgi.pa.server.unit.language.usecase;

import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.language.usecase.FindAllLanguages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindAllLanguagesTest {

    @Mock
    private LanguageDao mockLanguageDao;

    private FindAllLanguages sut;

    @BeforeEach
    void setup() {
        sut = new FindAllLanguages(mockLanguageDao);
    }

    @Test
    void should_call_languageDao_to_get_all_languages() {
        sut.execute();

        verify(mockLanguageDao, times(1)).findAll();
    }

    @Test
    void should_return_set_of_languages() {
        var languages = List.of(
                new Language().setId(1L).setLanguageName(LanguageName.C).setFileExtension("c"),
                new Language().setId(2L).setLanguageName(LanguageName.JAVA).setFileExtension("java")
        );
        when(mockLanguageDao.findAll()).thenReturn(languages);

        var result = sut.execute();

        var expectedSetLanguages = Set.copyOf(languages);
        assertThat(result).isEqualTo(expectedSetLanguages);
    }
}