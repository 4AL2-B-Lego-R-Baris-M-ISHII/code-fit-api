package fr.esgi.pa.server.unit.exercise.usecase;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise.core.entity.Exercise;
import fr.esgi.pa.server.exercise.core.entity.ExerciseCase;
import fr.esgi.pa.server.exercise.core.entity.ExerciseTest;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.adapter.ExerciseAdapter;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.adapter.ExerciseCaseAdapter;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.adapter.ExerciseTestAdapter;
import fr.esgi.pa.server.exercise.usecase.FindOneExercise;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindOneExerciseTest {

    private final long userId = 2L;
    private final long exerciseId = 11L;
    private FindOneExercise sut;

    @Mock
    private UserDao mockUserDao;

    @Mock
    private ExerciseDao mockExerciseDao;

    @Mock
    private ExerciseCaseDao mockExerciseCaseDao;

    @Mock
    private ExerciseTestDao mockExerciseTestDao;

    @Mock
    private LanguageDao mockLanguageDao;

    private final ExerciseAdapter exerciseAdapter = new ExerciseAdapter();
    private final ExerciseCaseAdapter exerciseCaseAdapter = new ExerciseCaseAdapter();
    private final ExerciseTestAdapter exerciseTestAdapter = new ExerciseTestAdapter();

    @BeforeEach
    void setup() {
        sut = new FindOneExercise(
                mockUserDao,
                mockExerciseDao,
                mockExerciseCaseDao,
                mockExerciseTestDao,
                mockLanguageDao,
                exerciseAdapter,
                exerciseCaseAdapter,
                exerciseTestAdapter
        );
    }

    @Test
    void when_user_with_given_userId_not_exists_should_throw_NotFoundException() {
        when(mockUserDao.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> sut.execute(exerciseId, userId))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage(String.format("%s : User with userId '%d' not found", sut.getClass(), userId));
    }

    @Test
    void when_found_exercise_should_call_list_exercise_cases() throws NotFoundException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundExercise = new Exercise()
                .setId(exerciseId)
                .setTitle("title")
                .setDescription("description")
                .setUserId(userId);
        when(mockExerciseDao.findById(exerciseId)).thenReturn(foundExercise);

        sut.execute(exerciseId, userId);

        verify(mockExerciseCaseDao, times(1)).findAllByExerciseId(exerciseId);
    }

    @Test
    void when_find_all_exercise_case_return_one_case_by_exercise_id_should_call_exerciseTestDao_to_get_all_test_per_case() throws NotFoundException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundExercise = new Exercise()
                .setId(exerciseId)
                .setTitle("title")
                .setDescription("description")
                .setUserId(userId);
        when(mockExerciseDao.findById(exerciseId)).thenReturn(foundExercise);
        var setExerciseCase = Set.of(
                new ExerciseCase()
                        .setId(789L)
                        .setExerciseId(exerciseId)
                        .setSolution("solution")
                        .setIsValid(false)
                        .setLanguageId(2L)
                        .setStartContent("start")
        );
        when(mockExerciseCaseDao.findAllByExerciseId(exerciseId)).thenReturn(setExerciseCase);

        sut.execute(exerciseId, userId);

        verify(mockExerciseTestDao, times(1)).findAllByExerciseCaseId(789L);
    }

    @Test
    void when_find_all_exercise_cases_and_return_few_cases_by_exercise_id_should_call_number_of_cases_exerciseTestDao_to_get_all_test_per_case() throws NotFoundException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundExercise = new Exercise()
                .setId(exerciseId)
                .setTitle("title")
                .setDescription("description")
                .setUserId(userId);
        when(mockExerciseDao.findById(exerciseId)).thenReturn(foundExercise);
        var setExerciseCase = Set.of(
                new ExerciseCase()
                        .setId(789L)
                        .setExerciseId(exerciseId)
                        .setSolution("solution")
                        .setIsValid(false)
                        .setLanguageId(2L)
                        .setStartContent("start"),
                new ExerciseCase()
                        .setId(123L)
                        .setExerciseId(exerciseId)
                        .setSolution("solution 123")
                        .setIsValid(false)
                        .setLanguageId(2L)
                        .setStartContent("start 123")
        );
        when(mockExerciseCaseDao.findAllByExerciseId(exerciseId)).thenReturn(setExerciseCase);

        sut.execute(exerciseId, userId);

        verify(mockExerciseTestDao, times(1)).findAllByExerciseCaseId(789L);
        verify(mockExerciseTestDao, times(1)).findAllByExerciseCaseId(123L);
    }

    @Test
    void when_find_all_exercise_test_per_case_return_few_tests_should_return_dto_exercise_with_cases_and_tests() throws NotFoundException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundExercise = new Exercise()
                .setId(exerciseId)
                .setTitle("title")
                .setDescription("description")
                .setUserId(userId);
        when(mockExerciseDao.findById(exerciseId)).thenReturn(foundExercise);
        var exerciseCase789 = new ExerciseCase()
                .setId(789L)
                .setExerciseId(exerciseId)
                .setSolution("solution")
                .setIsValid(false)
                .setLanguageId(2L)
                .setStartContent("start");
        var exerciseCase123 = new ExerciseCase()
                .setId(123L)
                .setExerciseId(exerciseId)
                .setSolution("solution 123")
                .setIsValid(false)
                .setLanguageId(2L)
                .setStartContent("start 123");
        var setExerciseCase = Set.of(
                exerciseCase789,
                exerciseCase123
        );
        when(mockExerciseCaseDao.findAllByExerciseId(exerciseId)).thenReturn(setExerciseCase);
        var setExerciseTestOfExercise789 = Set.of(
                new ExerciseTest()
                        .setId(3L)
                        .setExerciseCaseId(789L)
                        .setContent("test content 3"),
                new ExerciseTest()
                        .setId(4L)
                        .setExerciseCaseId(789L)
                        .setContent("test content 4")
        );
        when(mockExerciseTestDao.findAllByExerciseCaseId(789L)).thenReturn(setExerciseTestOfExercise789);
        var setExerciseTEstOfExercise123 = Set.of(
                new ExerciseTest()
                        .setId(5L)
                        .setExerciseCaseId(123L)
                        .setContent("test content 5")
        );
        when(mockExerciseTestDao.findAllByExerciseCaseId(123L)).thenReturn(setExerciseTEstOfExercise123);
        var java = new Language().setId(3L).setLanguageName(LanguageName.JAVA).setFileExtension("java");
        when(mockLanguageDao.findById(2L)).thenReturn(java);

        var result = sut.execute(exerciseId, userId);

        var expectedDtoExercise = exerciseAdapter.domainToDto(foundExercise);
        var expectedDtoExerciseTestOfExercise789 = setExerciseTestOfExercise789.stream()
                .map(exerciseTestAdapter::domainToDto).collect(Collectors.toSet());
        var expectedDtoExerciseCase789 = exerciseCaseAdapter.domainToDto(exerciseCase789);
        expectedDtoExerciseCase789.setTests(expectedDtoExerciseTestOfExercise789);
        expectedDtoExerciseCase789.setLanguage(java);
        var expectedDtoExerciseTestOfExercise123 = setExerciseTEstOfExercise123.stream()
                .map(exerciseTestAdapter::domainToDto).collect(Collectors.toSet());
        var expectedDtoExerciseCase123 = exerciseCaseAdapter.domainToDto(exerciseCase123);
        expectedDtoExerciseCase123.setTests(expectedDtoExerciseTestOfExercise123);
        expectedDtoExerciseCase123.setLanguage(java);

        expectedDtoExercise.setCases(Set.of(expectedDtoExerciseCase789, expectedDtoExerciseCase123));

        assertThat(result).isEqualTo(expectedDtoExercise);
    }
}