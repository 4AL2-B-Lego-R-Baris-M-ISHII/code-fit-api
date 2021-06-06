package fr.esgi.pa.server.integration.exercise_case.infrastructure.dataprovider.dao;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.dao.JpaExerciseTestDao;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseCase;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseTest;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.mapper.ExerciseTestMapper;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseCaseRepository;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseTestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class JpaExerciseTestDaoTest {

    @Autowired
    private JpaExerciseTestDao sut;

    @Autowired
    private ExerciseCaseRepository exerciseCaseRepository;

    @Autowired
    private ExerciseTestRepository exerciseTestRepository;

    @Autowired
    private ExerciseTestMapper exerciseTestMapper;

    @Test
    void test_if_exerciseTestRepository_method_findAllByExerciseCaseId_work() throws NotFoundException {
        var otherExerciseCaseToSave = new JpaExerciseCase()
                .setExerciseId(4L)
                .setIsValid(false)
                .setLanguageId(7L)
                .setSolution("other solution")
                .setStartContent("other start content");
        var savedOtherExerciseCase = exerciseCaseRepository.save(otherExerciseCaseToSave);
        var setOtherExerciseTestToSave = new JpaExerciseTest()
                .setContent("other content")
                .setExerciseCaseId(savedOtherExerciseCase.getId());
        exerciseTestRepository.save(setOtherExerciseTestToSave);

        var exerciseCaseToSave = new JpaExerciseCase()
                .setExerciseId(4L)
                .setIsValid(false)
                .setSolution("solution")
                .setLanguageId(7L)
                .setStartContent("start content");
        var savedExerciseCase = exerciseCaseRepository.save(exerciseCaseToSave);

        var setExerciseTestToSave = Set.of(
                new JpaExerciseTest()
                        .setContent("content 1")
                        .setExerciseCaseId(savedExerciseCase.getId()),
                new JpaExerciseTest()
                        .setContent("content 2")
                        .setExerciseCaseId(savedExerciseCase.getId())
        );
        var savedSetExerciseTest = exerciseTestRepository.saveAll(setExerciseTestToSave);

        var expected = savedSetExerciseTest.stream()
                .map(exerciseTestMapper::entityToDomain)
                .collect(Collectors.toSet());

        var result = sut.findAllByExerciseCaseId(savedExerciseCase.getId());

        assertThat(result).isEqualTo(expected);
    }
}