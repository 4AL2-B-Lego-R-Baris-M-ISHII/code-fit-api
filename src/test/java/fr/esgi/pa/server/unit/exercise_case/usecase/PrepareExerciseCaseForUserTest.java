package fr.esgi.pa.server.unit.exercise_case.usecase;

import fr.esgi.pa.server.code.core.adapter.CodeAdapter;
import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseTest;
import fr.esgi.pa.server.exercise_case.usecase.PrepareExerciseCaseForUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
class PrepareExerciseCaseForUserTest {
    private final long exerciseCaseId = 531L;
    private final long userId = 651L;
    private PrepareExerciseCaseForUser sut;

    @Mock
    private CodeDao mockCodeDao;

    @Autowired
    private CodeAdapter codeAdapter;

    @BeforeEach
    void setup() {
        sut = new PrepareExerciseCaseForUser(mockCodeDao, codeAdapter);
    }

    @Test
    void when_notAdminPart_is_true_should_found_code_by_exercise_case_id_and_user_id() {
        var dtoExerciseCase = new DtoExerciseCase()
                .setId(exerciseCaseId)
                .setCodes(null)
                .setStartContent("start content")
                .setIsValid(true)
                .setTests(Set.of(new DtoExerciseTest().setId(65L)));
        sut.execute(userId, dtoExerciseCase, true);

        verify(mockCodeDao, times(1))
                .findByUserIdAndExerciseCaseId(userId, exerciseCaseId);
    }

    @Test
    void when_notAdminPart_is_false_should_not_found_code_by_exercise_case_id_and_user_id_and_return_currnet_exercise_case() {
        var dtoExerciseCase = new DtoExerciseCase()
                .setId(exerciseCaseId)
                .setCodes(null)
                .setStartContent("start content")
                .setIsValid(true)
                .setTests(Set.of(new DtoExerciseTest().setId(65L)));
        var result = sut.execute(userId, dtoExerciseCase, false);

        verify(mockCodeDao, never())
                .findByUserIdAndExerciseCaseId(userId, exerciseCaseId);

        assertThat(result).isEqualTo(dtoExerciseCase);
    }

    @Test
    void when_found_code_should_set_code_to_dto_exercise_case_and_remove_tests() {
        var dtoExerciseCase = new DtoExerciseCase()
                .setId(exerciseCaseId)
                .setCodes(null)
                .setStartContent("start content")
                .setIsValid(true)
                .setTests(Set.of(new DtoExerciseTest().setId(65L)));

        var foundCode = new Code().setId(38L)
                .setExerciseCaseId(exerciseCaseId)
                .setUserId(userId)
                .setContent("code content")
                .setIsResolved(false);
        when(mockCodeDao.findByUserIdAndExerciseCaseId(userId, exerciseCaseId))
                .thenReturn(Optional.of(foundCode));

        var result = sut.execute(userId, dtoExerciseCase, true);

        var expectedCode = codeAdapter.domainToDto(foundCode);
        var expected = new DtoExerciseCase()
                .setId(exerciseCaseId)
                .setCodes(null)
                .setStartContent("start content")
                .setIsValid(true)
                .setTests(null)
                .setCodes(Set.of(expectedCode));

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void when_not_found_code_should_set_empty_codes_to_dto_exercise_case_and_remove_tests() {
        var dtoExerciseCase = new DtoExerciseCase()
                .setId(exerciseCaseId)
                .setCodes(null)
                .setStartContent("start content")
                .setIsValid(true)
                .setTests(Set.of(new DtoExerciseTest().setId(65L)));

        when(mockCodeDao.findByUserIdAndExerciseCaseId(userId, exerciseCaseId))
                .thenReturn(Optional.empty());

        var result = sut.execute(userId, dtoExerciseCase, true);

        var expected = new DtoExerciseCase()
                .setId(exerciseCaseId)
                .setStartContent("start content")
                .setIsValid(true)
                .setTests(null)
                .setCodes(null);

        assertThat(result).isEqualTo(expected);
    }
}