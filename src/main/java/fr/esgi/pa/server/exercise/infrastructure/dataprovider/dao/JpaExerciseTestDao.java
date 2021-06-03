package fr.esgi.pa.server.exercise.infrastructure.dataprovider.dao;

import fr.esgi.pa.server.exercise.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise.core.entity.ExerciseTest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class JpaExerciseTestDao implements ExerciseTestDao {
    @Override
    public Set<ExerciseTest> findAllByExerciseCaseId(Long exerciseCaseId) {
        return null;
    }
}
