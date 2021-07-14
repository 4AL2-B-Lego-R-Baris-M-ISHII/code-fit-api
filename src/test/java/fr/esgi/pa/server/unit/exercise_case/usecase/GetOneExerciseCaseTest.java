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
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.adapter.ExerciseCaseAdapter;
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.adapter.ExerciseTestAdapter;
import fr.esgi.pa.server.exercise_case.usecase.GetOneExerciseCase;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.user.core.dao.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOneExerciseCaseTest {
    private final long exerciseCaseId = 56L;
    private final long languageId = 12L;
    private final long userId = 45L;
    private final long exerciseId = 91L;
    private GetOneExerciseCase sut;

    @Mock
    private ExerciseCaseDao mockExerciseCaseDao;

    @Mock
    private UserDao mockUserDao;

    @Mock
    private ExerciseDao mockExerciseDao;

    @Mock
    private LanguageDao mockLanguageDao;

    @Mock
    private ExerciseTestDao mockExerciseTestDao;

    private final ExerciseCaseAdapter exerciseCaseAdapter = new ExerciseCaseAdapter();

    private final ExerciseTestAdapter exerciseTestAdapter = new ExerciseTestAdapter();

    @BeforeEach
    void setup() {
        sut = new GetOneExerciseCase(
                mockExerciseCaseDao,
                mockUserDao,
                mockExerciseDao,
                mockLanguageDao,
                mockExerciseTestDao,
                exerciseCaseAdapter,
                exerciseTestAdapter
        );
    }

    @Test
    void when_exercise_case_not_valid_and_user_not_exists_should_throw_NotFoundException() throws NotFoundException {
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setLanguageId(languageId)
                .setStartContent("start content")
                .setSolution("solution")
                .setIsValid(false);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);
        when(mockUserDao.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> sut.execute(userId, exerciseCaseId))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage(
                        "%s : User with id '%d' not found",
                        CommonExceptionState.NOT_FOUND,
                        userId
                );
    }

    @Test
    void when_exercise_case_is_not_valid_and_user_is_not_creator_of_concerned_exercise_case_should_throw_ForbiddenException() throws NotFoundException {
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setLanguageId(languageId)
                .setStartContent("start content")
                .setSolution("solution")
                .setIsValid(false);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var exercise = new Exercise()
                .setId(exerciseId)
                .setUserId(168L)
                .setTitle("exercise title")
                .setDescription("exercise description");
        when(mockExerciseDao.findById(exerciseId)).thenReturn(exercise);

        assertThatThrownBy(() -> sut.execute(userId, exerciseCaseId))
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage(
                        "%s : When exercise case is not valid, only creator can get current exercise case",
                        CommonExceptionState.FORBIDDEN
                );
    }

    @Test
    void when_exercise_case_found_should_found_language_by_id_of_found_exercise_case() throws NotFoundException, ForbiddenException {
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setLanguageId(languageId)
                .setStartContent("start content")
                .setSolution("solution")
                .setIsValid(true);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);

        sut.execute(userId, exerciseCaseId);

        verify(mockLanguageDao, times(1)).findById(languageId);
    }

    @Test
    void when_exercise_case_found_should_call_exercise_test_dao_by_exercise_case_id() throws NotFoundException, ForbiddenException {
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setLanguageId(languageId)
                .setStartContent("start content")
                .setSolution("solution")
                .setIsValid(true);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);

        sut.execute(userId, exerciseCaseId);

        verify(mockExerciseTestDao, times(1)).findAllByExerciseCaseId(exerciseCaseId);
    }

    @Test
    void when_found_language_of_exercise_case_and_tests_should_return_set_exercise_test() throws NotFoundException, ForbiddenException {
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setLanguageId(languageId)
                .setStartContent("start content")
                .setSolution("solution")
                .setIsValid(true);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);
        var foundLanguage = new Language()
                .setId(languageId)
                .setFileExtension("java")
                .setLanguageName(LanguageName.JAVA8);
        when(mockLanguageDao.findById(languageId)).thenReturn(foundLanguage);
        var setTest = Set.of(
                new ExerciseTest()
                        .setId(87L)
                        .setExerciseCaseId(exerciseCaseId)
                        .setContent("test content")
        );
        when(mockExerciseTestDao.findAllByExerciseCaseId(exerciseCaseId))
                .thenReturn(setTest);

        var result = sut.execute(userId, exerciseCaseId);

        var expected = exerciseCaseAdapter.domainToDto(foundExerciseCase);
        var expectedSetTest = setTest.stream()
                .map(exerciseTestAdapter::domainToDto)
                .collect(Collectors.toSet());
        expected.setLanguage(foundLanguage);
        expected.setTests(expectedSetTest);

        assertThat(result).isEqualTo(expected);
    }
}