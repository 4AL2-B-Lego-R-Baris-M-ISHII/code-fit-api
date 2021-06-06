package fr.esgi.pa.server.unit.exercise_case.infrastructure.dataprovider.dao;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.dao.JpaExerciseTestDao;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseTest;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.mapper.ExerciseTestMapper;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseCaseRepository;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseTestRepository;
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
class JpaExerciseTestDaoTest {
    private final long exerciseCaseId = 31L;
    @Mock
    private ExerciseCaseRepository mockExerciseCaseRepository;

    @Mock
    private ExerciseTestRepository mockExerciseTestRepository;

    private final ExerciseTestMapper exerciseTestMapper = new ExerciseTestMapper();

    private JpaExerciseTestDao sut;

    @BeforeEach
    void setup() {
        sut = new JpaExerciseTestDao(mockExerciseCaseRepository, mockExerciseTestRepository, exerciseTestMapper);
    }

    @Test
    void when_exercise_case_not_exists_should_throw_not_found_exception() {
        when(mockExerciseCaseRepository.existsById(exerciseCaseId)).thenReturn(false);

        assertThatThrownBy(() -> sut.findAllByExerciseCaseId(exerciseCaseId))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage("%s : exercise case with id '%d' not found", NotFoundException.class, exerciseCaseId);
    }

    @Test
    void when_exercise_case_exists_should_find_all_exercise_tests_by_exercise_case_id() throws NotFoundException {
        when(mockExerciseCaseRepository.existsById(exerciseCaseId)).thenReturn(true);

        sut.findAllByExerciseCaseId(exerciseCaseId);

        verify(mockExerciseTestRepository, times(1)).findAllByExerciseCaseId(exerciseCaseId);
    }

    @Test
    void when_call_exercise_test_repository_to_find_all_by_exercise_case_id_should_return_set_of_exercise_test() throws NotFoundException {
        when(mockExerciseCaseRepository.existsById(exerciseCaseId)).thenReturn(true);
        var setExerciseTest = Set.of(
                new JpaExerciseTest()
                        .setId(1L)
                        .setExerciseCaseId(exerciseCaseId)
                        .setContent("first content"),
                new JpaExerciseTest()
                        .setId(2L)
                        .setExerciseCaseId(exerciseCaseId)
                        .setContent("second content")
        );
        when(mockExerciseTestRepository.findAllByExerciseCaseId(exerciseCaseId)).thenReturn(setExerciseTest);

        var result = sut.findAllByExerciseCaseId(exerciseCaseId);

        var expected = setExerciseTest
                .stream().map(exerciseTestMapper::entityToDomain)
                .collect(Collectors.toSet());
        assertThat(result).isEqualTo(expected);
    }
}