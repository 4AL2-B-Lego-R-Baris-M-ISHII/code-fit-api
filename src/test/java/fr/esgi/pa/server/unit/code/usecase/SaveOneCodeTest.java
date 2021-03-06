package fr.esgi.pa.server.unit.code.usecase;

import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.code.usecase.SaveOneCode;
import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;
import fr.esgi.pa.server.user.core.dao.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveOneCodeTest {
    private final long userId = 34L;
    private final long exerciseCaseId = 86L;
    private final String codeContent = "code content";
    private SaveOneCode sut;

    @Mock
    private UserDao mockUserDao;

    @Mock
    private ExerciseCaseDao mockExerciseCaseDao;

    @Mock
    private CodeDao mockCodeDao;

    @BeforeEach
    void setup() {
        sut = new SaveOneCode(mockUserDao, mockExerciseCaseDao, mockCodeDao);
    }

    @Test
    void when_user_not_exists_with_given_userId_should_throw_NotFoundException() {
        when(mockUserDao.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> sut.execute(userId, exerciseCaseId, codeContent))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage(
                        "%s : User with id '%d' not found",
                        CommonExceptionState.NOT_FOUND,
                        userId
                );
    }

    @Test
    void when_exerciseCase_exists_but_not_valid_should_throw_NotFoundException() throws NotFoundException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var exerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(65L)
                .setSolution("solution")
                .setStartContent("start content")
                .setLanguageId(6L)
                .setIsValid(false);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(exerciseCase);

        assertThatThrownBy(() -> sut.execute(userId, exerciseCaseId, codeContent))
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage(
                        "%s : Can't save code of not valid exercise case",
                        CommonExceptionState.FORBIDDEN
                );
    }

    @Test
    void when_code_saved_should_return_saved_code_id() throws NotFoundException, ForbiddenException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var exerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(65L)
                .setSolution("solution")
                .setStartContent("start content")
                .setLanguageId(6L)
                .setIsValid(true);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(exerciseCase);
        var expectedCodeToSave = new Code()
                .setUserId(userId)
                .setExerciseCaseId(exerciseCaseId)
                .setContent(codeContent);
        var savedCode = new Code()
                .setId(76L)
                .setUserId(userId)
                .setExerciseCaseId(exerciseCaseId)
                .setContent(codeContent)
                .setIsResolved(false);
        when(mockCodeDao.save(expectedCodeToSave)).thenReturn(savedCode);

        var result = sut.execute(userId, exerciseCaseId, codeContent);

        assertThat(result).isEqualTo(savedCode.getId());
    }
}