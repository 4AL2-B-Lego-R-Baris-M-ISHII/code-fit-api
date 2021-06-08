package fr.esgi.pa.server.unit.exercise_case.usecase;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;
import fr.esgi.pa.server.exercise_case.usecase.VerifyExerciseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerifyExerciseCaseTest {
    private final long exerciseCaseId = 5L;
    private final long userId = 12L;
    private final long exerciseId = 32L;
    private final long languageId = 946l;
    private final String solution = "solution of exercise";
    private final String startContent = "start content";
    private VerifyExerciseCase sut;

    @Mock
    private ExerciseCaseDao mockExerciseCaseDao;

    @Mock
    private ExerciseTestDao mockExerciseTestDao;

    @BeforeEach
    void setup() {
        sut = new VerifyExerciseCase(mockExerciseCaseDao, mockExerciseTestDao);
    }

    @Test
    void should_call_exerciseCaseDao_to_find_exerciseCase_by_id() throws NotFoundException {
        sut.execute(exerciseCaseId);

        verify(mockExerciseCaseDao, times(1)).findById(exerciseCaseId);
    }

    @Test
    void when_exerciseCase_found_should_find_all_tests_by_exerciseCaseId() throws NotFoundException {
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setLanguageId(languageId)
                .setIsValid(false)
                .setSolution(solution)
                .setStartContent(startContent);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);

        sut.execute(exerciseCaseId);

        verify(mockExerciseTestDao, times(1)).findAllByExerciseCaseId(exerciseCaseId);
    }

    @Test
    void when_set_exerciseTest_found_should_execute_all_tests_depend_to_solution() {

    }
}