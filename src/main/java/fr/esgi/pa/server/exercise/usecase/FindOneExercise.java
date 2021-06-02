package fr.esgi.pa.server.exercise.usecase;

import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.core.entity.Exercise;
import fr.esgi.pa.server.user.core.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindOneExercise {
    private final UserDao userDao;
    private final ExerciseDao exerciseDao;

    public Exercise execute(Long exerciseId, Long userId) {
        return null;
    }
}
