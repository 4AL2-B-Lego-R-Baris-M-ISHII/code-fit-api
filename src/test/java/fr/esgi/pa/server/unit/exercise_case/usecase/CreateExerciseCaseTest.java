package fr.esgi.pa.server.unit.exercise_case.usecase;

import fr.esgi.pa.server.common.core.exception.AlreadyCreatedException;
import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.core.entity.Exercise;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;
import fr.esgi.pa.server.exercise_case.usecase.CreateExerciseCase;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.user.core.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateExerciseCaseTest {
    private final long userId = 98L;
    private final long exerciseId = 32L;
    private final long languageId = 3L;
    private CreateExerciseCase sut;

    @Mock
    private UserDao mockUserDao;

    @Mock
    private ExerciseDao mockExerciseDao;

    @Mock
    private ExerciseCaseDao mockExerciseCaseDao;

    @Mock
    private LanguageDao mockLanguageDao;


    @BeforeEach
    void setup() {
        sut = new CreateExerciseCase(mockUserDao, mockExerciseDao, mockExerciseCaseDao, mockLanguageDao);
    }

    @Test
    void when_user_not_exists_should_throw_NotFoundException() {
        when(mockUserDao.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> sut.execute(userId, exerciseId, languageId))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage("%s : User with id '%d' not exists", CommonExceptionState.NOT_FOUND, userId);
    }

    @Test
    void when_user_id_not_correspond_to_exercise_should_throw_ForbiddenException() throws NotFoundException {
        var notCreatorUserId = 12L;
        assertThat(userId).isNotEqualTo(notCreatorUserId);
        var foundExercise = new Exercise()
                .setId(exerciseId)
                .setTitle("title")
                .setDescription("description")
                .setUserId(userId);
        when(mockExerciseDao.findById(exerciseId)).thenReturn(foundExercise);
        when(mockUserDao.existsById(notCreatorUserId)).thenReturn(true);


        assertThatThrownBy(() -> sut.execute(notCreatorUserId, exerciseId, languageId))
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage(
                        "%s : Exercise case has to create by creator of concerned exercise",
                        CommonExceptionState.FORBIDDEN
                );
    }

    @Test
    void when_user_is_creator_of_exercise_should_find_language_by_id() throws NotFoundException, ForbiddenException, AlreadyCreatedException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundExercise = new Exercise()
                .setId(exerciseId)
                .setTitle("title")
                .setDescription("description")
                .setUserId(userId);
        when(mockExerciseDao.findById(exerciseId)).thenReturn(foundExercise);

        sut.execute(userId, exerciseId, languageId);

        verify(mockLanguageDao, times(1)).findById(languageId);
    }

    @Test
    void when_found_language_should_find_all_exercise_cases_by_exercise_id() throws NotFoundException, ForbiddenException, AlreadyCreatedException {
        var foundExercise = new Exercise()
                .setId(exerciseId)
                .setTitle("title")
                .setDescription("description")
                .setUserId(userId);
        when(mockExerciseDao.findById(exerciseId)).thenReturn(foundExercise);
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundLanguage = new Language()
                .setId(languageId)
                .setFileExtension("java")
                .setLanguageName(LanguageName.JAVA);
        when(mockLanguageDao.findById(languageId)).thenReturn(foundLanguage);

        sut.execute(userId, exerciseId, languageId);

        verify(mockExerciseCaseDao, times(1)).findAllByExerciseId(exerciseId);
    }

    @Test
    void when_one_exercise_case_has_language_correspond_to_language_id_should_throw_ForbiddenException() throws NotFoundException, ForbiddenException {
        var foundExercise = new Exercise()
                .setId(exerciseId)
                .setTitle("title")
                .setDescription("description")
                .setUserId(userId);
        when(mockExerciseDao.findById(exerciseId)).thenReturn(foundExercise);
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundLanguage = new Language()
                .setId(languageId)
                .setFileExtension("java")
                .setLanguageName(LanguageName.JAVA);
        when(mockLanguageDao.findById(languageId)).thenReturn(foundLanguage);
        var exerciseCase = new ExerciseCase()
                .setId(23L)
                .setExerciseId(exerciseId)
                .setLanguageId(languageId)
                .setSolution("solution")
                .setStartContent("start content")
                .setIsValid(false);
        when(mockExerciseCaseDao.findAllByExerciseId(exerciseId)).thenReturn(Set.of(exerciseCase));

        assertThatThrownBy(() -> sut.execute(userId, exerciseId, languageId))
                .isExactlyInstanceOf(AlreadyCreatedException.class)
                .hasMessage(
                        "%s : Exercise case with language '%s' already created",
                        CommonExceptionState.ALREADY_CREATED,
                        foundLanguage.getLanguageName()
                );
    }

}
