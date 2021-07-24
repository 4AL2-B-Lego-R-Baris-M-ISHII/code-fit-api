package fr.esgi.pa.server.exercise.usecase;

import fr.esgi.pa.server.exercise.core.dto.DtoExercise;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AddLoggedUserCodeAllExercises {
    public Set<DtoExercise> execute(Set<DtoExercise> setDtoExercise, Long loggedUserId) {
        return null;
    }
}
