package fr.esgi.pa.server.unit.language.infrastructure.dataprovider;

import fr.esgi.pa.server.common.exception.AlreadyCreatedException;
import fr.esgi.pa.server.common.exception.NotFoundException;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.language.infrastructure.dataprovider.JpaLanguage;
import fr.esgi.pa.server.language.infrastructure.dataprovider.JpaLanguageDao;
import fr.esgi.pa.server.language.infrastructure.dataprovider.LanguageMapper;
import fr.esgi.pa.server.language.infrastructure.dataprovider.LanguageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaLanguageDaoTest {
    @Mock
    private LanguageRepository mockLanguageRepository;

    private final LanguageMapper languageMapper = new LanguageMapper();

    JpaLanguageDao sut;

    @BeforeEach
    void setup() {
        sut = new JpaLanguageDao(mockLanguageRepository, languageMapper);
    }

    @Nested
    class CreateLanguage {

        @Test
        void when_language_with_language_name_already_created_should_throw_AlreadyCreatedException() {
            var foundLanguage = new JpaLanguage().setId(1L).setName(LanguageName.C).setFileExtension("c");
            when(mockLanguageRepository.findByName(LanguageName.C)).thenReturn(Optional.ofNullable(foundLanguage));

            assertThatThrownBy(() -> sut.createLanguage(LanguageName.C, "c"))
                    .isExactlyInstanceOf(AlreadyCreatedException.class)
                    .hasMessage(JpaLanguageDao.class + " : language with language name '" + LanguageName.C + "' already created");
        }

        @Test
        void when_language_saved_should_return_id_of_new_language() throws AlreadyCreatedException {
            var languageToSave = new JpaLanguage().setName(LanguageName.C).setFileExtension("c");
            var savedLanguage = new JpaLanguage()
                    .setId(1L)
                    .setName(languageToSave.getName())
                    .setFileExtension(languageToSave.getFileExtension());
            when(mockLanguageRepository.findByName(LanguageName.C)).thenReturn(Optional.empty());
            when(mockLanguageRepository.save(languageToSave)).thenReturn(savedLanguage);

            var result = sut.createLanguage(LanguageName.C, "c");

            assertThat(result).isEqualTo(1L);
        }
    }

    @Nested
    class FindByName {
        @Test
        void when_language_name_not_found_should_throw_NotFoundException() {
            when(mockLanguageRepository.findByName(LanguageName.C)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.findByName(LanguageName.C))
                    .isExactlyInstanceOf(NotFoundException.class)
                    .hasMessage(JpaLanguageDao.class + " : language name '" + LanguageName.C + "' not found");
        }

        @Test
        void when_language_found_should_return_language() throws NotFoundException {
            var foundLanguage = new JpaLanguage().setId(2L).setName(LanguageName.C).setFileExtension("c");
            var expected = languageMapper.entityToDomain(foundLanguage);
            when(mockLanguageRepository.findByName(LanguageName.C)).thenReturn(Optional.of(foundLanguage));

            var result = sut.findByName(LanguageName.C);

            assertThat(result).isEqualTo(expected);
        }
    }
}