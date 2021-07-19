package fr.esgi.pa.server.unit.exercise.usecase;

import fr.esgi.pa.server.code.core.dto.DtoCode;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.core.dto.DtoExercise;
import fr.esgi.pa.server.exercise.core.entity.Exercise;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    @Test
    void when_all_exercise_found_should_return_dto_exercise_with_corresponding_dto_exercise_case() {
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
        var exercise1 = new Exercise()
                .setId(exerciseCase1.getExerciseId())
                .setTitle("title exercise 1")
                .setDescription("description exercise 1")
                .setUserId(46L);
        var exercise2 = new Exercise()
                .setId(exerciseCase2.getExerciseId())
                .setTitle("title exercise 2")
                .setDescription("description exercise 2")
                .setUserId(56L);
        when(mockExerciseDao.findAllByIdIn(
                Set.of(exerciseCase1.getExerciseId(), exerciseCase2.getExerciseId())
        )).thenReturn(Set.of(exercise1, exercise2));

        var result = sut.execute(setDtoExerciseCase);

        var expectedDtoExercise1 = new DtoExercise()
                .setId(exercise1.getId())
                .setDescription(exercise1.getDescription())
                .setTitle(exercise1.getTitle())
                .setCases(Set.of(dtoExerciseCase1));
        var expectedDtoExercise2 = new DtoExercise()
                .setId(exercise2.getId())
                .setDescription(exercise2.getDescription())
                .setTitle(exercise2.getTitle())
                .setCases(Set.of(dtoExerciseCase2));
        var expectedResult = Set.of(expectedDtoExercise1, expectedDtoExercise2);
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void when_all_exercise_case_are_same_exercise_should_return_set_one_dto_exercise() {
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
                .setExerciseId(7L)
                .setLanguageId(4L)
                .setIsValid(true)
                .setSolution(dtoExerciseCase2.getSolution())
                .setStartContent(dtoExerciseCase2.getStartContent());
        when(mockExerciseCaseDao.findAllByIdIn(Set.of(3L, 5L))).thenReturn(
                Set.of(exerciseCase1, exerciseCase2)
        );
        var exercise1 = new Exercise()
                .setId(exerciseCase1.getExerciseId())
                .setTitle("title exercise 1")
                .setDescription("description exercise 1")
                .setUserId(46L);
        when(mockExerciseDao.findAllByIdIn(
                Set.of(exercise1.getId())
        )).thenReturn(Set.of(exercise1));

        var result = sut.execute(setDtoExerciseCase);

        var expectedDtoExercise = new DtoExercise()
                .setId(exercise1.getId())
                .setDescription(exercise1.getDescription())
                .setTitle(exercise1.getTitle())
                .setCases(Set.of(dtoExerciseCase1, dtoExerciseCase2));
        var expectedResult = Set.of(expectedDtoExercise);
        assertThat(result).isEqualTo(expectedResult);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.stream().findFirst().isPresent()).isEqualTo(true);
        assertThat(result.stream().findFirst().get()).isEqualTo(expectedDtoExercise);
    }
}