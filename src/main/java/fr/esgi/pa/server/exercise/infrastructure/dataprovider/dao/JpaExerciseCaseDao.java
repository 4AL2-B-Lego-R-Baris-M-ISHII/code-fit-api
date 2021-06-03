package fr.esgi.pa.server.exercise.infrastructure.dataprovider.dao;

import fr.esgi.pa.server.exercise.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise.core.entity.ExerciseCase;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseCaseRepository;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class JpaExerciseCaseDao implements ExerciseCaseDao {
    private ExerciseRepository exerciseRepository;
    private ExerciseCaseRepository exerciseCaseRepository;

    @Override
    public Set<ExerciseCase> findAllByExerciseId(Long exerciseId) {
        return null;
    }
}
