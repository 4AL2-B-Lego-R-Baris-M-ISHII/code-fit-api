package fr.esgi.pa.server.unit.exercise.usecase;

import fr.esgi.pa.server.code.core.adapter.CodeAdapter;
import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.code.infrastructure.entrypoint.adapter.CodeAdapterImpl;
import fr.esgi.pa.server.exercise.core.dto.DtoExercise;
import fr.esgi.pa.server.exercise.usecase.AddLoggedUserCodeAllExercises;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.user.core.dto.DtoUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddLoggedUserCodeAllExercisesTest {
    private final long loggedUserId = 654L;
    private AddLoggedUserCodeAllExercises sut;

    @Mock
    private CodeDao mockCodeDao;

    private final CodeAdapter codeAdapter = new CodeAdapterImpl();

    @BeforeEach
    void setup() {
        sut = new AddLoggedUserCodeAllExercises(mockCodeDao, codeAdapter);
    }

    @Test
    void when_setDtoExercise_contain_one_dto_exercise_with_one_exercise_case_should_call() {
        var dtoExerciseCase = new DtoExerciseCase()
                .setId(984L)
                .setLanguage(new Language().setId(3L).setLanguageName(LanguageName.C11).setFileExtension("c"))
                .setStartContent("start content")
                .setSolution("solution")
                .setIsValid(true);
        var dtoExercise = new DtoExercise()
                .setId(65L)
                .setUser(new DtoUser()).setTitle("title exercise")
                .setDescription("description")
                .setCases(
                        Set.of(dtoExerciseCase)
                );
        var dtoSetExercise = Set.of(dtoExercise);

        sut.execute(dtoSetExercise, loggedUserId);

        verify(mockCodeDao, times(1))
                .findByUserIdAndExerciseCaseId(loggedUserId, dtoExerciseCase.getId());
    }

    @Test
    void when_set_code_on_exercise_case_of_logged_user_should_return_dtoExercise_with_code() {
        var dtoExerciseCase = new DtoExerciseCase()
                .setId(984L)
                .setLanguage(new Language().setId(3L).setLanguageName(LanguageName.C11).setFileExtension("c"))
                .setStartContent("start content")
                .setSolution("solution")
                .setIsValid(true);
        var dtoExercise = new DtoExercise()
                .setId(65L)
                .setUser(new DtoUser()).setTitle("title exercise")
                .setDescription("description")
                .setCases(
                        Set.of(dtoExerciseCase)
                );
        var dtoSetExercise = Set.of(dtoExercise);
        var foundCode = new Code()
                .setId(35L)
                .setContent("code content")
                .setExerciseCaseId(dtoExerciseCase.getId())
                .setIsResolved(true)
                .setUserId(loggedUserId);
        var maybeCode = Optional.of(foundCode);
        when(mockCodeDao.findByUserIdAndExerciseCaseId(loggedUserId, dtoExerciseCase.getId()))
                .thenReturn(maybeCode);

        var result = sut.execute(dtoSetExercise, loggedUserId);

        assertThat(result).isNotNull();

        var expectedCode = codeAdapter.domainToDto(foundCode);
        result.forEach(curDtoExercise -> {
            assertThat(curDtoExercise).isNotNull();
            curDtoExercise.getCases().forEach(curCase -> {
                assertThat(curCase).isNotNull();
                curCase.getCodes().forEach(curCode -> {
                    assertThat(curCode).isNotNull();
                    assertThat(curCode).isEqualTo(expectedCode);
                });
            });
        });
    }

    @Test
    void when_codeDao_return_empty_optional_should_return_dtoExercise_with_empty_code() {
        var dtoExerciseCase = new DtoExerciseCase()
                .setId(984L)
                .setLanguage(new Language().setId(3L).setLanguageName(LanguageName.C11).setFileExtension("c"))
                .setStartContent("start content")
                .setSolution("solution")
                .setIsValid(true);
        var dtoExercise = new DtoExercise()
                .setId(65L)
                .setUser(new DtoUser()).setTitle("title exercise")
                .setDescription("description")
                .setCases(
                        Set.of(dtoExerciseCase)
                );
        var dtoSetExercise = Set.of(dtoExercise);
        when(mockCodeDao.findByUserIdAndExerciseCaseId(loggedUserId, dtoExerciseCase.getId()))
                .thenReturn(Optional.empty());

        var result = sut.execute(dtoSetExercise, loggedUserId);

        assertThat(result).isNotNull();
        result.forEach(curDtoExercise -> {
            assertThat(curDtoExercise).isNotNull();
            curDtoExercise.getCases().forEach(curCase -> {
                assertThat(curCase).isNotNull();
                assertThat(curCase.getCodes().size()).isEqualTo(0);
            });
        });
    }
}