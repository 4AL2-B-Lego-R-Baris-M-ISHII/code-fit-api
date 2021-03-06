package fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.dao;

import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseTest;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.mapper.ExerciseTestMapper;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JpaExerciseTestDao implements ExerciseTestDao {
    private final ExerciseTestRepository exerciseTestRepository;
    private final ExerciseTestMapper exerciseTestMapper;

    @Override
    public Set<ExerciseTest> findAllByExerciseCaseId(Long exerciseCaseId) {
        return exerciseTestRepository.findAllByExerciseCaseId(exerciseCaseId)
                .stream()
                .map(exerciseTestMapper::entityToDomain)
                .collect(Collectors.toSet());
    }

    @Override
    public void deleteAllByExerciseCaseId(Long exerciseCaseId) {
        var setTest = exerciseTestRepository.findAllByExerciseCaseId(exerciseCaseId);
        exerciseTestRepository.deleteAll(setTest);
    }

    @Override
    public Set<ExerciseTest> saveAll(Set<ExerciseTest> setTest) {
        var jpaSetTest = setTest.stream()
                .map(exerciseTestMapper::domainToEntity)
                .collect(Collectors.toSet());
        return exerciseTestRepository.saveAll(jpaSetTest).stream()
                .map(exerciseTestMapper::entityToDomain)
                .collect(Collectors.toSet());
    }
}
