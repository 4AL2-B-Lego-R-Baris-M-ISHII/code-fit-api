package fr.esgi.pa.server.exercise.usecase;

import fr.esgi.pa.server.exercise.core.dto.DtoExercise;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilterExercisesByCreator {
    public Set<DtoExercise> execute(Set<DtoExercise> setExercise, Long creatorId) {
        return setExercise.stream()
                .filter(dtoExercise -> dtoExercise
                        .getUser()
                        .getId().equals(creatorId)
                )
                .collect(Collectors.toSet());
    }
}
