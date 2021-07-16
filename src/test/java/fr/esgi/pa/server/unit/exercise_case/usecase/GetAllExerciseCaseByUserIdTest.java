package fr.esgi.pa.server.unit.exercise_case.usecase;

import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.usecase.GetAllExerciseCaseByUserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllExerciseCaseByUserIdTest {
    private final long userId = 61L;
    private GetAllExerciseCaseByUserId sut;

    @Mock
    private CodeDao mockCodeDao;

    @Mock
    private ExerciseCaseDao mockExerciseCaseDao;

    @BeforeEach
    void setup() {
        sut = new GetAllExerciseCaseByUserId(mockCodeDao, mockExerciseCaseDao);
    }

    @Test
    void should_get_all_code_by_user_id() {
        sut.execute(userId);

        verify(mockCodeDao, times(1)).findAllByUserId(userId);
    }

    @Test
    void when_get_all_code_by_user_id_should_return_set_exercise_case() {
//        var exerciseCase1 = new ExerciseCase()
//                .setId(123L)
//                .setLanguageId(1L)
//                .setExerciseId(7L)
//                .setStartContent("start content")
//                .setSolution("solution");
//        var exerciseCase12 = new ExerciseCase()
//                .setId(124L)
//                .setLanguageId(2L)
//                .setExerciseId(8L)
//                .setStartContent("start content2")
//                .setSolution("solution2");
//        var setExerciseCase = Set.of(
//                exerciseCase1, exerciseCase12
//        );

        var code7 = new Code().setId(7L)
                .setUserId(userId)
                .setExerciseCaseId(123L)
                .setContent("code content7")
                .setIsResolved(true);
        var code8 = new Code().setId(8L)
                .setUserId(userId)
                .setExerciseCaseId(123L)
                .setContent("code content8")
                .setIsResolved(true);
        var code9 = new Code().setId(9L)
                .setUserId(userId)
                .setExerciseCaseId(124L)
                .setContent("code content9")
                .setIsResolved(true);
        var setCode = Set.of(code7, code8, code9);
        when(mockCodeDao.findAllByUserId(userId)).thenReturn(setCode);

        sut.execute(userId);

        verify(mockExerciseCaseDao, times(1)).findAllByIdIn(Set.of(123L, 124L));
    }
}