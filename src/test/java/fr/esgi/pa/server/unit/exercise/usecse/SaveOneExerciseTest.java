package fr.esgi.pa.server.unit.exercise.usecse;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveOneExerciseTest {

    private final String title = "title";
    private final String description = "description";
    private final String languageStr = "JAVA";
    @Mock
    private UserDao mockUserDao;

    @Mock
    private LanguageDao mockLanguageDao;

    @Mock
    private ExerciseDao mockExerciseDao;

    private SaveOneExercise sut;

    @BeforeEach
    void setup() {
        sut = new SaveOneExercise(mockUserDao, mockLanguageDao, mockExerciseDao);
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
        when(mockUserDao.existsById(3L)).thenReturn(true);

        sut.execute(title, description, languageStr, 3L);

        verify(mockLanguageDao, times(1)).findByStrLanguage(languageStr);
    }

    @Test
    void should_call_create_of_exercise_dao() throws NotFoundException, IncorrectLanguageNameException {
        when(mockUserDao.existsById(3L)).thenReturn(true);
        var language = new Language()
                .setId(2L)
                .setLanguageName(LanguageName.JAVA)
                .setFileExtension("java");
        when(mockLanguageDao.findByStrLanguage(languageStr)).thenReturn(language);

        sut.execute(title, description, languageStr, 3L);

        verify(mockExerciseDao, times(1)).saveExercise(title, description, language, 3L);
    }
}