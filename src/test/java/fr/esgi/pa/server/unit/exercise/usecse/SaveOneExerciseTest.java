package fr.esgi.pa.server.unit.exercise.usecse;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.util.DefaultExercise;
import fr.esgi.pa.server.exercise.usecase.SaveOneExercise;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.language.core.exception.IncorrectLanguageNameException;
import fr.esgi.pa.server.user.core.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveOneExerciseTest {

    private final String title = "title";
    private final String description = "description";
    private final String languageStr = "JAVA";
    private final long userId = 3L;
    @Mock
    private UserDao mockUserDao;

    @Mock
    private LanguageDao mockLanguageDao;

    @Mock
    private DefaultExercise mockDefaultExercise;

    private SaveOneExercise sut;

    @BeforeEach
    void setup() {
        sut = new SaveOneExercise(mockUserDao, mockLanguageDao, mockDefaultExercise);
    }

    @Test
    void when_user_with_given_id_not_found_should_throw_not_found_exception() {
        when(mockUserDao.existsById(2L)).thenReturn(false);

        assertThatThrownBy(() -> sut.execute(title, description, languageStr, 2L))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage(String.format("%s : User with id '%d' not exists", sut.getClass(), 2L));
    }

    @Test
    void should_get_language_by_language_str_and_call_language_dao() throws NotFoundException, IncorrectLanguageNameException {
        when(mockUserDao.existsById(userId)).thenReturn(true);

        sut.execute(title, description, languageStr, userId);

        verify(mockLanguageDao, times(1)).findByStrLanguage(languageStr);
    }

    @Test
    void when_language_found_should_create_exercise_with_defaults_values_depend_to_language() throws NotFoundException, IncorrectLanguageNameException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var language = new Language()
                .setId(1L)
                .setLanguageName(LanguageName.C)
                .setFileExtension("c");
        when(mockLanguageDao.findByStrLanguage(languageStr)).thenReturn(language);

        sut.execute(title, description, languageStr, userId);

        verify(mockDefaultExercise, times(1)).createDefaultExercise(title, description, language, userId);
    }

    @Test
    void when_exercise_with_default_values_create_should_return_exercise_id() throws NotFoundException, IncorrectLanguageNameException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var language = new Language()
                .setId(1L)
                .setLanguageName(LanguageName.C)
                .setFileExtension("c");
        when(mockLanguageDao.findByStrLanguage(languageStr)).thenReturn(language);
        var exerciseId = 5L;
        when(mockDefaultExercise.createDefaultExercise(title, description, language, userId)).thenReturn(exerciseId);

        var result = sut.execute(title, description, languageStr, userId);

        assertThat(result).isEqualTo(exerciseId);
    }
}