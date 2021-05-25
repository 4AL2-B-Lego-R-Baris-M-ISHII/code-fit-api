package fr.esgi.pa.server.exercise.infrastructure.dataprovider;

import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.language.core.Language;
import org.springframework.stereotype.Service;

@Service
public class JpaExerciseDao implements ExerciseDao {
    @Override
    public Long saveExercise(String title, String description, Language language) {
        return null;
    }
}
