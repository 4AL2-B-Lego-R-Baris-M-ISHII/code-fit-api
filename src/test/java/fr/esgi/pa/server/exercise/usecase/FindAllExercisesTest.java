package fr.esgi.pa.server.exercise.usecase;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.core.entity.Exercise;
import fr.esgi.pa.server.exercise.core.entity.ExerciseCase;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.adapter.ExerciseAdapter;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.adapter.ExerciseCaseAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindAllExercisesTest {

    private FindAllExercises sut;

    @Mock
    private ExerciseDao mockExerciseDao;

    @Mock
    private ExerciseCaseDao mockExerciseCaseDao;

    private final ExerciseAdapter exerciseAdapter = new ExerciseAdapter();
    private final ExerciseCaseAdapter exerciseCaseAdapter = new ExerciseCaseAdapter();

    @BeforeEach
    void setup() {
        sut = new FindAllExercises(mockExerciseDao, mockExerciseCaseDao, exerciseAdapter, exerciseCaseAdapter);
    }

    @Test
    void should_call_findAll_of_exerciseDao() throws NotFoundException {
        sut.execute();

        verify(mockExerciseDao, times(1)).findAll();
    }

    @Test
    void when_get_all_exercises_should_return_all_exercises() throws NotFoundException {
        var exercises = Set.of(
                new Exercise()
                        .setId(3L)
                        .setUserId(7L)
                        .setTitle("title3")
                        .setDescription("description3"),
                new Exercise()
                        .setId(6L)
                        .setUserId(7L)
                        .setTitle("title6")
                        .setDescription("description6")
        );
        when(mockExerciseDao.findAll()).thenReturn(exercises);

        var result = sut.execute();

        var expected = exercises.stream()
                .map(exerciseAdapter::domainToDto)
                .collect(Collectors.toSet());

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void when_exercises_contain_cases_should_return_all_exercises_with_cases() throws NotFoundException {
        var exercise = new Exercise()
                .setId(3L)
                .setTitle("title3")
                .setDescription("description3");
        var exercises = Set.of(exercise);
        when(mockExerciseDao.findAll()).thenReturn(exercises);
        var exerciseCase = new ExerciseCase()
                .setId(5L)
                .setExerciseId(3L)
                .setIsValid(false)
                .setSolution("solution")
                .setLanguageId(2L)
                .setStartContent("start content");
        var cases = Set.of(exerciseCase);
        when(mockExerciseCaseDao.findAllByExerciseId(3L)).thenReturn(cases);

        var result = sut.execute();

        var expectedDtoExercise = exerciseAdapter.domainToDto(exercise);
        expectedDtoExercise.setCases(cases.stream().map(exerciseCaseAdapter::domainToDto).collect(Collectors.toSet()));
        var expected = Set.of(expectedDtoExercise);

        assertThat(result).isEqualTo(expected);
    }
}