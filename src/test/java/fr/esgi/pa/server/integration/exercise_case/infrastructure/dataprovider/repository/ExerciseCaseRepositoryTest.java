package fr.esgi.pa.server.integration.exercise_case.infrastructure.dataprovider.repository;

import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExercise;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseCase;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseCaseRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ExerciseCaseRepositoryTest {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseCaseRepository sut;

    @Nested
    class DeleteAllByExerciseIdTest {
        @Test
        void should_delete_all_exerciseCase_by_id() {
            var exerciseToSave = new JpaExercise().setUserId(3L).setTitle("title").setDescription("description");
            var savedExercise = exerciseRepository.save(exerciseToSave);

            var setCaseToSave = Set.of(
                    new JpaExerciseCase()
                            .setExerciseId(savedExercise.getId())
                            .setIsValid(false)
                            .setSolution("solution")
                            .setLanguageId(5L)
                            .setStartContent("start content")
            );

            var savedSetCase = sut.saveAll(setCaseToSave);

            var foundSetCase = sut.findAllByExerciseId(savedExercise.getId());

            assertThat(foundSetCase.size()).isEqualTo(setCaseToSave.size());

            sut.deleteAll(savedSetCase);

            var checkSetCases = sut.findAllByExerciseId(savedExercise.getId());

            assertThat(checkSetCases).isNotNull();
            assertThat(checkSetCases.size()).isEqualTo(0);
        }
    }
}