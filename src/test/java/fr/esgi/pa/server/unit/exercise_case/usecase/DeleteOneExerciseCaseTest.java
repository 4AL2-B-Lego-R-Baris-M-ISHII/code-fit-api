package fr.esgi.pa.server.unit.exercise_case.usecase;

import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.usecase.DeleteOneExerciseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteOneExerciseCaseTest {

    private final long exerciseCaseId = 195L;
    private DeleteOneExerciseCase sut;

    @Mock
    private ExerciseCaseDao mockExerciseCaseDao;

    @Mock
    private ExerciseTestDao mockExerciseTestDao;

    @BeforeEach
    void setup() {
        sut = new DeleteOneExerciseCase(mockExerciseCaseDao, mockExerciseTestDao);
    }

    @Test
    void when_exercise_case_not_exists_should_throw_NotFoundException() {
        when(mockExerciseCaseDao.existsById(exerciseCaseId)).thenReturn(false);

        assertThatThrownBy(() -> sut.execute(exerciseCaseId))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage(
                        "%s : Exercise case with id '%d' not found",
                        CommonExceptionState.NOT_FOUND,
                        exerciseCaseId
                );
    }

    @Test
    void when_exercise_case_exists_should_delete_all_tests_by_exercise_case_id() throws NotFoundException {
        when(mockExerciseCaseDao.existsById(exerciseCaseId)).thenReturn(true);

        sut.execute(exerciseCaseId);

        verify(mockExerciseTestDao, times(1)).deleteAllByExerciseCaseId(exerciseCaseId);
    }

    @Test
    void when_set_exercise_test_delete_all_should_delete_exercise_case() throws NotFoundException {
        when(mockExerciseCaseDao.existsById(exerciseCaseId)).thenReturn(true);
        doNothing().when(mockExerciseTestDao).deleteAllByExerciseCaseId(exerciseCaseId);

        sut.execute(exerciseCaseId);

        verify(mockExerciseCaseDao, times(1)).deleteById(exerciseCaseId);
    }
}