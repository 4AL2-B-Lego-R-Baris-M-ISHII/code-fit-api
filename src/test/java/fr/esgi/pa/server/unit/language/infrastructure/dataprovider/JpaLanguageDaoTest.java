package fr.esgi.pa.server.unit.language.infrastructure.dataprovider;

import fr.esgi.pa.server.common.core.exception.AlreadyCreatedException;
import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.language.core.exception.IncorrectLanguageNameException;
import fr.esgi.pa.server.language.core.exception.LanguageExceptionState;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

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
                    .hasMessage(
                            "%s : language with language name '%s' already created",
                            CommonExceptionState.ALREADY_CREATED,
                            LanguageName.C
                    );
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
    class FindByLanguageName {
        @Test
        void when_language_name_not_found_should_throw_NotFoundException() {
            when(mockLanguageRepository.findByName(LanguageName.C)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.findByLanguageName(LanguageName.C))
                    .isExactlyInstanceOf(NotFoundException.class)
                    .hasMessage(
                            "%s : language name '%s' not found",
                            CommonExceptionState.NOT_FOUND,
                            LanguageName.C
                    );
        }

        @Test
        void when_language_found_should_return_language() throws NotFoundException {
            var foundLanguage = new JpaLanguage().setId(2L).setName(LanguageName.C).setFileExtension("c");
            var expected = languageMapper.entityToDomain(foundLanguage);
            when(mockLanguageRepository.findByName(LanguageName.C)).thenReturn(Optional.of(foundLanguage));

            var result = sut.findByLanguageName(LanguageName.C);

            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    class FindByStrLanguage {
        @Test
        void when_language_not_correspond_to_given_language_name_enum_should_throw_IncorrectLanguageNameException() {
            var incorrectLanguage = "incorrect language";
            assertThatThrownBy(() -> sut.findByStrLanguage(incorrectLanguage))
                    .isExactlyInstanceOf(IncorrectLanguageNameException.class)
                    .hasMessage(
                            String.format(
                                    "%s : Language '%s' is incorrect",
                                    LanguageExceptionState.INCORRECT_LANGUAGE_NAME,
                                    incorrectLanguage
                            )
                    );
        }

        @Test
        void should_return_language_when_found() throws IncorrectLanguageNameException, NotFoundException {
            var foundLanguage = new JpaLanguage().setId(2L).setName(LanguageName.C).setFileExtension("c");
            var expected = languageMapper.entityToDomain(foundLanguage);
            when(mockLanguageRepository.findByName(LanguageName.C)).thenReturn(Optional.of(foundLanguage));

            var result = sut.findByStrLanguage("C");

            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    class FindLanguageById {
        @Test
        void when_return_language_by_repository_is_empty_should_throw_not_found_exception() {
            var languageId = 28L;
            when(mockLanguageRepository.findById(languageId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.findById(languageId))
                    .isExactlyInstanceOf(NotFoundException.class)
                    .hasMessage(
                            "%s : language with id '%d' not found",
                            CommonExceptionState.NOT_FOUND,
                            languageId
                    );
        }

        @Test
        void when_found_language_by_language_repository_should_return_found_language() throws NotFoundException {
            var languageId = 33L;
            var java = new JpaLanguage()
                    .setId(languageId)
                    .setFileExtension("java")
                    .setName(LanguageName.JAVA);
            when(mockLanguageRepository.findById(languageId)).thenReturn(Optional.of(java));

            var result = sut.findById(languageId);

            var expected = languageMapper.entityToDomain(java);
            assertThat(result).isEqualTo(expected);
        }
    }
}