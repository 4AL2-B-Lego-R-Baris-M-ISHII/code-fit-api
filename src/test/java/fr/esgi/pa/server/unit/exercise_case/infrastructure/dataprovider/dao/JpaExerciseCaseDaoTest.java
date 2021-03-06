package fr.esgi.pa.server.unit.exercise_case.infrastructure.dataprovider.dao;

import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.dao.JpaExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseCase;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.mapper.ExerciseCaseMapper;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseCaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaExerciseCaseDaoTest {
    private final long exerciseId = 77L;

    @Mock
    private ExerciseCaseRepository mockExerciseCaseRepository;

    @Mock
    private ExerciseTestDao mockExerciseTestDao;

    private final ExerciseCaseMapper exerciseCaseMapper = new ExerciseCaseMapper();

    private JpaExerciseCaseDao sut;

    @BeforeEach
    void setup() {
        sut = new JpaExerciseCaseDao(mockExerciseTestDao, mockExerciseCaseRepository, exerciseCaseMapper);
    }

    @Nested
    class FindAllByExerciseId {

        @Test
        void when_exercise_with_exercise_id_exists_should_call_exerciseCaseRepository_to_find_all_by_exerciseId() {
            sut.findAllByExerciseId(exerciseId);

            verify(mockExerciseCaseRepository, times(1)).findAllByExerciseId(exerciseId);
        }

        @Test
        void when_exerciseCaseRepository_fetch_all_cases_by_exerciseId_should_send_set_of_exercise_cases() {
            var setJpaExerciseCase = Set.of(
                    new JpaExerciseCase()
                            .setId(2L)
                            .setExerciseId(exerciseId)
                            .setSolution("solution case 2")
                            .setIsValid(false)
                            .setLanguageId(1L)
                            .setStartContent("start content 3"),
                    new JpaExerciseCase()
                            .setId(3L)
                            .setExerciseId(exerciseId)
                            .setSolution("solution case 3")
                            .setIsValid(false)
                            .setLanguageId(2L)
                            .setStartContent("start case 3")
            );
            when(mockExerciseCaseRepository.findAllByExerciseId(exerciseId)).thenReturn(setJpaExerciseCase);

            var result = sut.findAllByExerciseId(exerciseId);

            assertThat(result).isNotNull();
            var expectedSetExerciseCase = setJpaExerciseCase.stream()
                    .map(exerciseCaseMapper::entityToDomain)
                    .collect(Collectors.toSet());
            assertThat(result).isEqualTo(expectedSetExerciseCase);
        }
    }

    @Nested
    class DeleteAllByExerciseId {
        @Test
        void should_find_all_cases_by_exerciseId() {
            sut.deleteAllByExerciseId(exerciseId);

            verify(mockExerciseCaseRepository, times(1)).findAllByExerciseId(exerciseId);
        }

        @Test
        void when_found_all_cases_by_exerciseId_should_call_deleteAllByExerciseTest_of_exerciseTestDao() {
            var case1 = new JpaExerciseCase()
                    .setId(2L)
                    .setExerciseId(exerciseId)
                    .setLanguageId(2L)
                    .setSolution("solution")
                    .setIsValid(false)
                    .setStartContent("start content");
            var case2 = new JpaExerciseCase()
                    .setId(1L)
                    .setExerciseId(exerciseId)
                    .setLanguageId(2L)
                    .setSolution("solution")
                    .setIsValid(false)
                    .setStartContent("start content");
            var setCase = Set.of(case1, case2);
            when(mockExerciseCaseRepository.findAllByExerciseId(exerciseId)).thenReturn(setCase);

            sut.deleteAllByExerciseId(exerciseId);

            verify(mockExerciseTestDao, times(1)).deleteAllByExerciseCaseId(case1.getId());
            verify(mockExerciseTestDao, times(1)).deleteAllByExerciseCaseId(case2.getId());
        }

        @Test
        void when_delete_all_by_exercise_case_id_should_delete_exercise_case() {
            var case1 = new JpaExerciseCase()
                    .setId(2L)
                    .setExerciseId(exerciseId)
                    .setLanguageId(2L)
                    .setSolution("solution")
                    .setIsValid(false)
                    .setStartContent("start content");
            var case2 = new JpaExerciseCase()
                    .setId(1L)
                    .setExerciseId(exerciseId)
                    .setLanguageId(2L)
                    .setSolution("solution")
                    .setIsValid(false)
                    .setStartContent("start content");
            var setCase = Set.of(case1, case2);
            when(mockExerciseCaseRepository.findAllByExerciseId(exerciseId)).thenReturn(setCase);
            doNothing().when(mockExerciseTestDao).deleteAllByExerciseCaseId(case1.getId());
            doNothing().when(mockExerciseTestDao).deleteAllByExerciseCaseId(case2.getId());

            sut.deleteAllByExerciseId(exerciseId);

            verify(mockExerciseCaseRepository, times(1)).deleteAll(setCase);
        }
    }

    @Nested
    class FindByIdTest {
        private final long exerciseCaseId = 65L;

        @Test
        void when_exerciseCase_not_found_by_repository_should_throw_not_found_exception() {
            when(mockExerciseCaseRepository.findById(exerciseCaseId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.findById(exerciseCaseId))
                    .isExactlyInstanceOf(NotFoundException.class)
                    .hasMessage("%s : Exercise case with id '%d' not found", CommonExceptionState.NOT_FOUND, exerciseCaseId);
        }

        @Test
        void when_exerciseCase_found_should_return_domain_exerciseCase() throws NotFoundException {
            var jpaExerciseCase = new JpaExerciseCase()
                    .setId(exerciseCaseId)
                    .setExerciseId(77L)
                    .setSolution("one solution of exercise 77")
                    .setIsValid(false)
                    .setLanguageId(3L);
            when(mockExerciseCaseRepository.findById(exerciseCaseId)).thenReturn(Optional.of(jpaExerciseCase));

            var result = sut.findById(exerciseCaseId);

            var expected = exerciseCaseMapper.entityToDomain(jpaExerciseCase);
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    class DeleteById {

        private final long exerciseCaseId = 153L;

        @Test
        void when_exercise_case_not_exists_should_throw_NotFoundException() {
            when(mockExerciseCaseRepository.existsById(exerciseCaseId)).thenReturn(false);

            assertThatThrownBy(() -> sut.deleteById(exerciseCaseId))
                    .isExactlyInstanceOf(NotFoundException.class)
                    .hasMessage(
                            "%s : Exercise case with id '%d' not found",
                            CommonExceptionState.NOT_FOUND,
                            exerciseCaseId
                    );
        }

        @Test
        void when_exercise_case_exists_should_delete_exercise_case() throws NotFoundException {
            when(mockExerciseCaseRepository.existsById(exerciseCaseId)).thenReturn(true);

            sut.deleteById(exerciseCaseId);

            verify(mockExerciseCaseRepository, times(1)).deleteById(exerciseCaseId);
        }
    }
}