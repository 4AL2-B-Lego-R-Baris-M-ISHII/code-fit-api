package fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.dao;

import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.mapper.ExerciseCaseMapper;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JpaExerciseCaseDao implements ExerciseCaseDao {
    private final ExerciseTestDao exerciseTestDao;
    private final ExerciseCaseRepository exerciseCaseRepository;
    private final ExerciseCaseMapper exerciseCaseMapper;

    @Override
    public Set<ExerciseCase> findAllByExerciseId(Long exerciseId) {
        return exerciseCaseRepository.findAllByExerciseId(exerciseId)
                .stream()
                .map(exerciseCaseMapper::entityToDomain)
                .collect(Collectors.toSet());
    }

    @Override
    public void deleteAllByExerciseId(Long exerciseId) {
        var setCase = exerciseCaseRepository.findAllByExerciseId(exerciseId);
        setCase.forEach(jpaExerciseCase -> exerciseTestDao.deleteAllByExerciseCaseId(jpaExerciseCase.getId()));
        exerciseCaseRepository.deleteAll(setCase);
    }
}
