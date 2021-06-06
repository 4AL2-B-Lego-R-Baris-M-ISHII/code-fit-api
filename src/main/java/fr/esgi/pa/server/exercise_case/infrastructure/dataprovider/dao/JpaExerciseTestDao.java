package fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.dao;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseTest;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.mapper.ExerciseTestMapper;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseCaseRepository;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JpaExerciseTestDao implements ExerciseTestDao {
    private final ExerciseCaseRepository exerciseCaseRepository;
    private final ExerciseTestRepository exerciseTestRepository;
    private final ExerciseTestMapper exerciseTestMapper;

    @Override
    public Set<ExerciseTest> findAllByExerciseCaseId(Long exerciseCaseId) throws NotFoundException {
        if (!exerciseCaseRepository.existsById(exerciseCaseId)) {
            var message = String.format("%s : exercise case with id '%d' not found", NotFoundException.class, exerciseCaseId);
            throw new NotFoundException(message);
        }

        // TODO : test integration to confirm that is work in real
        return exerciseTestRepository.findAllByExerciseCaseId(exerciseCaseId)
                .stream()
                .map(exerciseTestMapper::entityToDomain)
                .collect(Collectors.toSet());
    }
}
