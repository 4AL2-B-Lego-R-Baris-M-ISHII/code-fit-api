package fr.esgi.pa.server.unit.exercise.usecase;

import fr.esgi.pa.server.code.core.dto.DtoCode;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.usecase.GetAllExerciseThatUserResolved;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseTest;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllExerciseThatUserResolvedTest {
    private GetAllExerciseThatUserResolved sut;

    @Mock
    private ExerciseCaseDao mockExerciseCaseDao;

    @Mock
    private ExerciseDao mockExerciseDao;

    @BeforeEach
    void setup() {
        sut = new GetAllExerciseThatUserResolved(
                mockExerciseCaseDao,
                mockExerciseDao
        );
    }

    @Test
    void should_find_all_exercise_case_by_exercise_case_id() {
        var dtoExerciseCase1 = new DtoExerciseCase()
                .setId(3L)
                .setTests(Set.of(new DtoExerciseTest().setId(7L)))
                .setSolution("solution")
                .setStartContent("start content")
                .setCodes(Set.of(new DtoCode().setCodeId(65L)));
        var dtoExerciseCase2 = new DtoExerciseCase()
                .setId(5L)
                .setTests(Set.of(new DtoExerciseTest().setId(68L)))
                .setSolution("solution other case")
                .setStartContent("start other content")
                .setCodes(Set.of(new DtoCode().setCodeId(986L)));
        var setDtoExerciseCase = Set.of(dtoExerciseCase1, dtoExerciseCase2);

        sut.execute(setDtoExerciseCase);

        verify(mockExerciseCaseDao, times(1)).findAllByIdIn(Set.of(3L, 5L));
    }

    @Test
    void when_get_all_exercise_case_should_get_all_exercise_by_found_set_exercise_case_exercise_id_properties() {
        var dtoExerciseCase1 = new DtoExerciseCase()
                .setId(3L)
                .setTests(Set.of(new DtoExerciseTest().setId(7L)))
                .setSolution("solution")
                .setStartContent("start content")
                .setCodes(Set.of(new DtoCode().setCodeId(65L)));
        var dtoExerciseCase2 = new DtoExerciseCase()
                .setId(5L)
                .setTests(Set.of(new DtoExerciseTest().setId(68L)))
                .setSolution("solution other case")
                .setStartContent("start other content")
                .setCodes(Set.of(new DtoCode().setCodeId(986L)));
        var setDtoExerciseCase = Set.of(dtoExerciseCase1, dtoExerciseCase2);
        var exerciseCase1 = new ExerciseCase()
                .setId(dtoExerciseCase1.getId())
                .setExerciseId(7L)
                .setLanguageId(6L)
                .setIsValid(true)
                .setSolution(dtoExerciseCase1.getSolution())
                .setStartContent(dtoExerciseCase1.getStartContent());
        var exerciseCase2 = new ExerciseCase()
                .setId(dtoExerciseCase2.getId())
                .setExerciseId(8L)
                .setLanguageId(4L)
                .setIsValid(true)
                .setSolution(dtoExerciseCase2.getSolution())
                .setStartContent(dtoExerciseCase2.getStartContent());
        when(mockExerciseCaseDao.findAllByIdIn(Set.of(3L, 5L))).thenReturn(
                Set.of(exerciseCase1, exerciseCase2)
        );

        sut.execute(setDtoExerciseCase);

        verify(mockExerciseDao, times(1)).findAllByIdIn(
                Set.of(exerciseCase1.getExerciseId(), exerciseCase2.getExerciseId())
        );
    }
}