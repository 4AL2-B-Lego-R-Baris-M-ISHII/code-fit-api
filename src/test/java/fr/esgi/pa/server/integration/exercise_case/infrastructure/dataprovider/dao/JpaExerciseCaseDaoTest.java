package fr.esgi.pa.server.integration.exercise_case.infrastructure.dataprovider.dao;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExercise;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.dao.JpaExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseCase;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.mapper.ExerciseCaseMapper;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseCaseRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DirtiesContext
@SpringBootTest
class JpaExerciseCaseDaoTest {
    @Autowired
    private JpaExerciseCaseDao sut;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseCaseRepository exerciseCaseRepository;

    @Autowired
    private ExerciseCaseMapper exerciseCaseMapper;

    @Nested
    class FindAllByExerciseIdTest {
        @Test
        void should_get_set_of_exercise_case_by_exercise_id() throws NotFoundException {
            var exerciseToSave = new JpaExercise()
                    .setTitle("title exercise")
                    .setDescription("title description")
                    .setUserId(4L);
            var savedExercise = exerciseRepository.save(exerciseToSave);
            var setExerciseCase = Set.of(
                    new JpaExerciseCase()
                            .setExerciseId(savedExercise.getId())
                            .setSolution("solution case 2")
                            .setIsValid(false)
                            .setLanguageId(1L)
                            .setStartContent("start content 3"),
                    new JpaExerciseCase()
                            .setExerciseId(savedExercise.getId())
                            .setSolution("solution case 3")
                            .setIsValid(false)
                            .setLanguageId(2L)
                            .setStartContent("start case 3")
            );
            var expectedJpaCases = exerciseCaseRepository.saveAll(setExerciseCase);
            var otherExercise = new JpaExercise()
                    .setTitle("other")
                    .setDescription("other")
                    .setUserId(4L);
            var savedOtherExercise = exerciseRepository.save(otherExercise);
            var otherExerciseCase = new JpaExerciseCase()
                    .setExerciseId(savedOtherExercise.getId())
                    .setSolution("solution")
                    .setLanguageId(1L)
                    .setIsValid(false)
                    .setStartContent("start other");
            exerciseCaseRepository.save(otherExerciseCase);

            var result = sut.findAllByExerciseId(savedExercise.getId());

            var expected = expectedJpaCases.stream()
                    .map(exerciseCaseMapper::entityToDomain)
                    .collect(Collectors.toSet());

            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    class FindAllByIdIn {

        private final long exerciseId = 49L;

        @Test
        void should_get_set_of_exercise_case_by_set_exercise_case_id() {
            var jpaExerciseCase1 = new JpaExerciseCase().setExerciseId(exerciseId).setLanguageId(7L).setSolution("solution").setStartContent("start content1").setIsValid(true);
            var jpaExerciseCase2 = new JpaExerciseCase().setExerciseId(9L).setLanguageId(7L).setSolution("solution").setStartContent("start content2").setIsValid(false);
            var listSavedExerciseCase = exerciseCaseRepository.saveAll(Set.of(jpaExerciseCase1, jpaExerciseCase2));
            var setId = listSavedExerciseCase.stream()
                    .map(JpaExerciseCase::getId)
                    .collect(Collectors.toSet());
            var jpaExercise3 = new JpaExerciseCase().setExerciseId(exerciseId).setLanguageId(7L).setSolution("solution").setStartContent("start content1").setIsValid(true);
            exerciseCaseRepository.save(jpaExercise3);
            ;

            var result = sut.findAllByIdIn(setId);

            var expectedResult = listSavedExerciseCase.stream()
                    .map(exerciseCaseMapper::entityToDomain)
                    .collect(Collectors.toSet());
            assertThat(result).isNotNull();
            assertThat(result.size()).isEqualTo(2);
            assertThat(result).isEqualTo(expectedResult);
        }
    }
}