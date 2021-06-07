package fr.esgi.pa.server.unit.exercise_case.usecase;

import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.core.entity.Exercise;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseTest;
import fr.esgi.pa.server.exercise_case.usecase.UpdateExerciseCase;
import fr.esgi.pa.server.user.core.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateExerciseCaseTest {
    private final long userId = 78L;
    private final long exerciseCaseId = 89L;
    private final String solution = "solution case";
    private final String startContent = "start content case";
    private final long exerciseId = 90L;
    private final long languageId = 2L;
    private UpdateExerciseCase sut;

    @Mock
    private UserDao mockUserDao;

    @Mock
    private ExerciseCaseDao mockExerciseCaseDao;

    @Mock
    private ExerciseDao mockExerciseDao;

    @Mock
    private ExerciseTestDao mockExerciseTestDao;

    @BeforeEach
    void setup() {
        sut = new UpdateExerciseCase(mockUserDao, mockExerciseCaseDao, mockExerciseDao, mockExerciseTestDao);
    }

    @Test
    void when_user_not_exists_should_throw_not_found_exception() {
        var setTest = Set.of(
                new ExerciseTest()
                        .setId(2L)
                        .setExerciseCaseId(exerciseCaseId)
                        .setContent("content test 2"),
                new ExerciseTest()
                        .setExerciseCaseId(exerciseCaseId)
                        .setContent("new content test")
        );
        when(mockUserDao.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> sut.execute(userId, exerciseCaseId, solution, startContent, setTest))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage("%s : User with id '%d' not found", CommonExceptionState.NOT_FOUND, userId);
    }

    @Test
    void when_exercise_userId_not_correspond_to_user_should_throw_ForbiddenException() throws NotFoundException {
        var setTest = Set.of(
                new ExerciseTest()
                        .setId(2L)
                        .setExerciseCaseId(exerciseCaseId)
                        .setContent("content test 2"),
                new ExerciseTest()
                        .setExerciseCaseId(exerciseCaseId)
                        .setContent("new content test")
        );
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setLanguageId(languageId)
                .setIsValid(false)
                .setSolution(solution)
                .setStartContent(startContent);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);
        var foundExercise = new Exercise()
                .setId(exerciseId)
                .setUserId(837L)
                .setTitle("title exercise")
                .setDescription("description exercise");
        assertThat(userId).isNotEqualTo(837L);
        when(mockExerciseDao.findById(exerciseId)).thenReturn(foundExercise);

        assertThatThrownBy(() -> sut.execute(userId, exerciseCaseId, solution, startContent, setTest))
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage(
                        "%s : Exercise case can be update by creator of exercise with id '%d'",
                        CommonExceptionState.FORBIDDEN,
                        foundExercise.getId());
    }

    @Test
    void when_set_test_empty_should_throw_forbidden_exception() throws NotFoundException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setLanguageId(languageId)
                .setIsValid(false)
                .setSolution("old solution")
                .setStartContent("old start content");
        assertThat(foundExerciseCase.getSolution()).isNotEqualTo(solution);
        assertThat(foundExerciseCase.getStartContent()).isNotEqualTo(startContent);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);
        var foundExercise = new Exercise()
                .setId(exerciseId)
                .setUserId(userId)
                .setTitle("title exercise")
                .setDescription("description exercise");
        when(mockExerciseDao.findById(exerciseId)).thenReturn(foundExercise);

        assertThatThrownBy(() -> sut.execute(userId, exerciseCaseId, solution, startContent, new HashSet<>()))
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage("%s : Set test can't be empty", ForbiddenException.class);
    }

    @Test
    void when_set_test_has_null_exerciseCaseId_property_should_throw_forbidden_exception() throws NotFoundException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setLanguageId(languageId)
                .setIsValid(false)
                .setSolution("old solution")
                .setStartContent("old start content");
        assertThat(foundExerciseCase.getSolution()).isNotEqualTo(solution);
        assertThat(foundExerciseCase.getStartContent()).isNotEqualTo(startContent);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);
        var foundExercise = new Exercise()
                .setId(exerciseId)
                .setUserId(userId)
                .setTitle("title exercise")
                .setDescription("description exercise");
        when(mockExerciseDao.findById(exerciseId)).thenReturn(foundExercise);
        var setTest = Set.of(
                new ExerciseTest()
                        .setId(2L)
                        .setContent("content test 2"),
                new ExerciseTest()
                        .setExerciseCaseId(exerciseCaseId)
                        .setContent("new content test")
        );

        assertThatThrownBy(() -> sut.execute(userId, exerciseCaseId, solution, startContent, setTest))
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage("%s : All exercise tests need concerned exercise case id", ForbiddenException.class);
    }

    @Test
    void when_set_test_has_different_exerciseCaseId_property_than_given_one_param_should_throw_forbidden_exception() throws NotFoundException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setLanguageId(languageId)
                .setIsValid(false)
                .setSolution("old solution")
                .setStartContent("old start content");
        assertThat(foundExerciseCase.getSolution()).isNotEqualTo(solution);
        assertThat(foundExerciseCase.getStartContent()).isNotEqualTo(startContent);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);
        var foundExercise = new Exercise()
                .setId(exerciseId)
                .setUserId(userId)
                .setTitle("title exercise")
                .setDescription("description exercise");
        when(mockExerciseDao.findById(exerciseId)).thenReturn(foundExercise);
        var setTest = Set.of(
                new ExerciseTest()
                        .setId(2L)
                        .setExerciseCaseId(exerciseCaseId)
                        .setContent("content test 2"),
                new ExerciseTest()
                        .setExerciseCaseId(321L)
                        .setContent("new content test")
        );
        assertThat(321L).isNotEqualTo(exerciseCaseId);
        assertThatThrownBy(() -> sut.execute(userId, exerciseCaseId, solution, startContent, setTest))
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage("%s : All exercise tests need concerned exercise case id", ForbiddenException.class);
    }

    @Test
    void when_set_test_correct_should_save_all_tests() throws NotFoundException, ForbiddenException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setLanguageId(languageId)
                .setIsValid(false)
                .setSolution("old solution")
                .setStartContent("old start content");
        assertThat(foundExerciseCase.getSolution()).isNotEqualTo(solution);
        assertThat(foundExerciseCase.getStartContent()).isNotEqualTo(startContent);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);
        var foundExercise = new Exercise()
                .setId(exerciseId)
                .setUserId(userId)
                .setTitle("title exercise")
                .setDescription("description exercise");
        when(mockExerciseDao.findById(exerciseId)).thenReturn(foundExercise);
        var setTest = Set.of(
                new ExerciseTest()
                        .setId(2L)
                        .setExerciseCaseId(exerciseCaseId)
                        .setContent("content test 2"),
                new ExerciseTest()
                        .setExerciseCaseId(exerciseCaseId)
                        .setContent("new content test")
        );

        sut.execute(userId, exerciseCaseId, solution, startContent, setTest);

        verify(mockExerciseTestDao, times(1)).saveAll(setTest);
    }

    @Test
    void when_set_test_saved_should_save_exercise_case() throws NotFoundException, ForbiddenException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setLanguageId(languageId)
                .setIsValid(false)
                .setSolution("old solution")
                .setStartContent("old start content");
        assertThat(foundExerciseCase.getSolution()).isNotEqualTo(solution);
        assertThat(foundExerciseCase.getStartContent()).isNotEqualTo(startContent);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);
        var foundExercise = new Exercise()
                .setId(exerciseId)
                .setUserId(userId)
                .setTitle("title exercise")
                .setDescription("description exercise");
        when(mockExerciseDao.findById(exerciseId)).thenReturn(foundExercise);
        var setTest = Set.of(
                new ExerciseTest()
                        .setId(2L)
                        .setExerciseCaseId(exerciseCaseId)
                        .setContent("content test 2"),
                new ExerciseTest()
                        .setExerciseCaseId(exerciseCaseId)
                        .setContent("new content test")
        );
        when(mockExerciseTestDao.saveAll(setTest)).thenReturn(setTest);

        sut.execute(userId, exerciseCaseId, solution, startContent, setTest);

        var expectedSave = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setLanguageId(languageId)
                .setIsValid(false)
                .setSolution(solution)
                .setStartContent(startContent);
        verify(mockExerciseCaseDao, times(1)).saveOne(expectedSave);
    }
}