package fr.esgi.pa.server.unit.exercise_case.infrastructure.dataprovider.dao;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.dao.JpaExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseCase;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.mapper.ExerciseCaseMapper;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseCaseRepository;
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
class JpaExerciseCaseDaoTest {
    private final long exerciseId = 77L;
    @Mock
    private ExerciseRepository mockExerciseRepository;

    @Mock
    private ExerciseCaseRepository mockExerciseCaseRepository;

    private final ExerciseCaseMapper exerciseCaseMapper = new ExerciseCaseMapper();

    private JpaExerciseCaseDao sut;

    @BeforeEach
    void setup() {
        sut = new JpaExerciseCaseDao(mockExerciseRepository, mockExerciseCaseRepository, exerciseCaseMapper);
    }

    @Test
    void when_exercise_not_found_with_exercise_id_should_throw_NotFoundException() {
        when(mockExerciseRepository.existsById(exerciseId)).thenReturn(false);

        assertThatThrownBy(() -> sut.findAllByExerciseId(exerciseId))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage("%s : exerciseId '%d' not found", NotFoundException.class, exerciseId);
    }

    @Test
    void when_exercise_with_exercise_id_exists_should_call_exerciseCaseRepository_to_find_all_by_exerciseId() throws NotFoundException {
        when(mockExerciseRepository.existsById(exerciseId)).thenReturn(true);

        sut.findAllByExerciseId(exerciseId);

        verify(mockExerciseCaseRepository, times(1)).findAllByExerciseId(exerciseId);
    }

    @Test
    void when_exerciseCaseRepository_fetch_all_cases_by_exerciseId_should_send_set_of_exercise_cases() throws NotFoundException {
        when(mockExerciseRepository.existsById(exerciseId)).thenReturn(true);
        var setJpaExerciseCase = Set.of(
                new JpaExerciseCase()
                        .setId(2L)
                        .setExerciseId(exerciseId)
                        .setSolution("solution case 2")
                        .setIsValid(false)
                        .setLanguageId(1L)
                        .setStartContent("start content 3"),
                new JpaExerciseCase()
                        .setId(3L)
                        .setExerciseId(exerciseId)
                        .setSolution("solution case 3")
                        .setIsValid(false)
                        .setLanguageId(2L)
                        .setStartContent("start case 3")
        );
        when(mockExerciseCaseRepository.findAllByExerciseId(exerciseId)).thenReturn(setJpaExerciseCase);

        var result = sut.findAllByExerciseId(exerciseId);

        assertThat(result).isNotNull();
        var expectedSetExerciseCase = setJpaExerciseCase.stream()
                .map(exerciseCaseMapper::entityToDomain)
                .collect(Collectors.toSet());
        assertThat(result).isEqualTo(expectedSetExerciseCase);
    }
}