package fr.esgi.pa.server.unit.code.infrastructure.dataprovider.dao;

import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.code.infrastructure.dataprovider.dao.JpaCodeDao;
import fr.esgi.pa.server.code.infrastructure.dataprovider.entity.JpaCode;
import fr.esgi.pa.server.code.infrastructure.dataprovider.mapper.CodeMapper;
import fr.esgi.pa.server.code.infrastructure.dataprovider.repository.CodeRepository;
import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaCodeDaoTest {
    private final long userId = 43L;
    private final long exerciseCaseId = 31L;
    private final String content = "a content";
    private JpaCodeDao sut;

    private final CodeMapper codeMapper = new CodeMapper();

    @Mock
    private CodeRepository mockCodeRepository;

    @BeforeEach
    void setup() {
        sut = new JpaCodeDao(mockCodeRepository, codeMapper);
    }

    @Test
    void when_code_not_contain_userId_should_throw_ForbiddenException() {
        var codeToSave = new Code()
                .setUserId(null)
                .setContent(content)
                .setExerciseCaseId(exerciseCaseId);
        assertThatThrownBy(() -> sut.save(codeToSave))
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage(
                        "%s : Code with null user id can't be saved",
                        CommonExceptionState.FORBIDDEN
                );
    }

    @Test
    void when_code_not_contain_exerciseCaseId_should_throw_ForbiddenException() {
        var codeToSave = new Code()
                .setUserId(userId)
                .setContent(content)
                .setExerciseCaseId(null);
        assertThatThrownBy(() -> sut.save(codeToSave))
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage(
                        "%s : Code with null exercise case id can't be saved",
                        CommonExceptionState.FORBIDDEN
                );
    }

    @Test
    void when_code_saved_should_return_saved_code() throws ForbiddenException {
        var codeToSave = new Code()
                .setUserId(userId)
                .setContent(content)
                .setExerciseCaseId(exerciseCaseId);
        var jpaCodeToSave = codeMapper.domainToEntity(codeToSave);
        var savedCode = new JpaCode()
                .setId(9L)
                .setContent(content)
                .setUserId(userId)
                .setExerciseCaseId(exerciseCaseId)
                .setIsResolved(false);
        when(mockCodeRepository.save(jpaCodeToSave)).thenReturn(savedCode);

        var result = sut.save(codeToSave);

        var expectedSavedCode = codeMapper.entityToDomain(savedCode);

        assertThat(result).isEqualTo(expectedSavedCode);
    }
}