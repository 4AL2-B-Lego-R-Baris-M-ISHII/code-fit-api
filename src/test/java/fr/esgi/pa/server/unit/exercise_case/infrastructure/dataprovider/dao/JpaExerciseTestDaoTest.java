package fr.esgi.pa.server.unit.exercise_case.infrastructure.dataprovider.dao;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.dao.JpaExerciseTestDao;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseTest;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.mapper.ExerciseTestMapper;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseTestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaExerciseTestDaoTest {
    private final long exerciseCaseId = 31L;

    @Mock
    private ExerciseTestRepository mockExerciseTestRepository;

    private final ExerciseTestMapper exerciseTestMapper = new ExerciseTestMapper();

    private JpaExerciseTestDao sut;

    @BeforeEach
    void setup() {
        sut = new JpaExerciseTestDao(mockExerciseTestRepository, exerciseTestMapper);
    }

    @Nested
    class FindAllByExerciseCaseId {

        @Test
        void when_exercise_case_exists_should_find_all_exercise_tests_by_exercise_case_id() {
            sut.findAllByExerciseCaseId(exerciseCaseId);

            verify(mockExerciseTestRepository, times(1)).findAllByExerciseCaseId(exerciseCaseId);
        }

        @Test
        void when_call_exercise_test_repository_to_find_all_by_exercise_case_id_should_return_set_of_exercise_test() throws NotFoundException {
            var setExerciseTest = Set.of(
                    new JpaExerciseTest()
                            .setId(1L)
                            .setExerciseCaseId(exerciseCaseId)
                            .setContent("first content"),
                    new JpaExerciseTest()
                            .setId(2L)
                            .setExerciseCaseId(exerciseCaseId)
                            .setContent("second content")
            );
            when(mockExerciseTestRepository.findAllByExerciseCaseId(exerciseCaseId)).thenReturn(setExerciseTest);

            var result = sut.findAllByExerciseCaseId(exerciseCaseId);

            var expected = setExerciseTest
                    .stream().map(exerciseTestMapper::entityToDomain)
                    .collect(Collectors.toSet());
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    class DeleteAllByExerciseCaseIdTest {
        @Test
        void should_find_all_test_by_exercise_case_id() {
            sut.deleteAllByExerciseCaseId(exerciseCaseId);

            verify(mockExerciseTestRepository, times(1)).findAllByExerciseCaseId(exerciseCaseId);
        }

        @Test
        void when_found_tests_should_delete_all_tests() {
            var test1 = new JpaExerciseTest()
                    .setId(5L)
                    .setContent("content 5")
                    .setExerciseCaseId(exerciseCaseId);
            var test2 = new JpaExerciseTest()
                    .setId(6L)
                    .setContent("content 6")
                    .setExerciseCaseId(exerciseCaseId);
            var setTest = Set.of(test1, test2);
            when(mockExerciseTestRepository.findAllByExerciseCaseId(exerciseCaseId))
                    .thenReturn(setTest);

            sut.deleteAllByExerciseCaseId(exerciseCaseId);

            verify(mockExerciseTestRepository, times(1)).deleteAll(setTest);
        }
    }
}