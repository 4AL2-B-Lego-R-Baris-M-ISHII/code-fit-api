package fr.esgi.pa.server.exercise.infrastructure.dataprovider.dao;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.core.entity.Exercise;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.mapper.ExerciseMapper;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExercise;
import fr.esgi.pa.server.user.core.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaExerciseDao implements ExerciseDao {
    private final UserDao userDao;
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
}
