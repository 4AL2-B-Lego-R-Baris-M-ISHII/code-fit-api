package fr.esgi.pa.server.exercise.infrastructure.dataprovider.dao;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise.core.entity.ExerciseCase;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.mapper.ExerciseCaseMapper;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseCaseRepository;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JpaExerciseCaseDao implements ExerciseCaseDao {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseCaseRepository exerciseCaseRepository;
    private final ExerciseCaseMapper exerciseCaseMapper;

    @Override
    public Set<ExerciseCase> findAllByExerciseId(Long exerciseId) throws NotFoundException {
        if (!exerciseRepository.existsById(exerciseId)) {
            var message = String.format("%s : exerciseId '%d' not found", NotFoundException.class, exerciseId);
            throw new NotFoundException(message);
        }

        return exerciseCaseRepository.findAllByExerciseId(exerciseId)
                .stream()
                .map(exerciseCaseMapper::entityToDomain)
                .collect(Collectors.toSet());
    }
}
