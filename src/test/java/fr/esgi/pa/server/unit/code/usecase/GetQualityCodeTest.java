package fr.esgi.pa.server.unit.code.usecase;

import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.core.dto.CodeQualityType;
import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.code.usecase.GetQualityCode;
import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.user.core.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Stack;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetQualityCodeTest {
    private final long userId = 61L;
    private final long codeId = 32L;
    private GetQualityCode sut;
    private Stack<CodeQualityType> stackType;

    @Mock
    private UserDao mockUserDao;

    @Mock
    private CodeDao mockCodeDao;
    private long exerciseCaseId;

    @BeforeEach
    void setup() {
        stackType = new Stack<>();
        stackType.push(CodeQualityType.LINES_CODE);

        sut = new GetQualityCode(mockUserDao, mockCodeDao);
    }

    @Test
    void when_user_not_exists_should_throw_not_found_exception() {
        when(mockUserDao.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> sut.execute(userId, codeId, stackType))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage(
                        "%s : User with id '%d' not found",
                        CommonExceptionState.NOT_FOUND,
                        userId
                );
    }

    @Test
    void when_code_found_and_is_not_user_one_should_throw_forbidden_exception() throws NotFoundException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var otherUserId = 31L;
        var foundCode = new Code()
                .setId(codeId)
                .setContent("code content")
                .setIsResolved(true)
                .setUserId(otherUserId)
                .setExerciseCaseId(exerciseCaseId);
        when(mockCodeDao.findById(codeId)).thenReturn(foundCode);
        assertThat(userId).isNotEqualTo(otherUserId);

        assertThatThrownBy(() -> sut.execute(userId, codeId, stackType))
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage(
                        "%s : Current user can't get quality code of other user's code",
                        CommonExceptionState.FORBIDDEN
                );
    }
}