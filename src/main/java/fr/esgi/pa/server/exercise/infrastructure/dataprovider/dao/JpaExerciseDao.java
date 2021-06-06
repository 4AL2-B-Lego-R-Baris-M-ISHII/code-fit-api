package fr.esgi.pa.server.exercise.infrastructure.dataprovider.dao;

import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.core.entity.Exercise;
import fr.esgi.pa.server.exercise.core.exception.IncorrectExerciseException;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExercise;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.mapper.ExerciseMapper;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.user.core.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JpaExerciseDao implements ExerciseDao {
    private final UserDao userDao;
    private final ExerciseCaseDao exerciseCaseDao;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;

    @Override
    public Exercise createExercise(String title, String description, Long userId) throws NotFoundException {
        if (!userDao.existsById(userId)) {
            var message = String.format("%s : User with user id '%d' not found", this.getClass(), userId);
            throw new NotFoundException(message);
        }
        var exerciseToSave = new JpaExercise().setTitle(title).setDescription(description);
        var savedExercise = exerciseRepository.save(exerciseToSave);

        return exerciseMapper.entityToDomain(savedExercise);
    }

    @Override
    public Exercise findById(Long exerciseId) throws NotFoundException {
        var foundExercise = exerciseRepository.findById(exerciseId).orElseThrow(() -> {
            var message = String.format("%s : Exercise with id '%d' not found", NotFoundException.class, exerciseId);
            return new NotFoundException(message);
        });
        return exerciseMapper.entityToDomain(foundExercise);
    }

    @Override
    public Set<Exercise> findAll() {
        var exercises = exerciseRepository.findAll();
        return exercises.stream()
                .map(exerciseMapper::entityToDomain)
                .collect(Collectors.toSet());
    }

    @Override
    public Boolean existsById(Long exerciseId) {
        return exerciseRepository.existsById(exerciseId);
    }

    @Override
    public Exercise save(Exercise exercise) throws IncorrectExerciseException {
        if (exercise == null) {
            var message = String.format("%s : Exercise is null", IllegalArgumentException.class);
            throw new IllegalArgumentException(message);
        }
        checkExercisePropertyNotNull(exercise.getTitle(), "%s : Exercise title property is null");
        checkExercisePropertyNotNull(exercise.getDescription(), "%s : Exercise description property is null");

        var exerciseToSave = exerciseMapper.domainToEntity(exercise);
        var savedExercise = exerciseRepository.save(exerciseToSave);
        return exerciseMapper.entityToDomain(savedExercise);
    }

    private void checkExercisePropertyNotNull(String stringProperty, String formatErrorMessage) throws IncorrectExerciseException {
        if (stringProperty == null) {
            var message = String.format(formatErrorMessage, IncorrectExerciseException.class);
            throw new IncorrectExerciseException(message);
        }
    }

    @Transactional
    @Override
    public void deleteById(Long exerciseId) throws NotFoundException {
        if (!exerciseRepository.existsById(exerciseId)) {
            var message = String.format("%s : Exercise with id '%d' not found", CommonExceptionState.NOT_FOUND, exerciseId);
            throw new NotFoundException(message);
        }

        exerciseCaseDao.deleteAllByExerciseId(exerciseId);

        exerciseRepository.deleteById(exerciseId);
    }
}
