package fr.esgi.pa.server.exercise.infrastructure.dataprovider;

import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.user.core.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaExerciseDao implements ExerciseDao {
    private final UserDao userDao;
    @Override
    public Long saveExercise(String title, String description, Language language, Long userId) {
        return null;
    }
}
