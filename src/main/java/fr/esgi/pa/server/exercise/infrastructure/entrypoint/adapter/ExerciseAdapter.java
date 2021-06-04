package fr.esgi.pa.server.exercise.infrastructure.entrypoint.adapter;

import fr.esgi.pa.server.common.core.mapper.MapperDomainToDto;
import fr.esgi.pa.server.exercise.core.dto.DtoExercise;
import fr.esgi.pa.server.exercise.core.entity.Exercise;
import org.springframework.stereotype.Component;

@Component
public class ExerciseAdapter implements MapperDomainToDto<Exercise, DtoExercise> {
    @Override
    public DtoExercise domainToDto(Exercise exercise) {
        return new DtoExercise()
                .setId(exercise.getId())
                .setUserId(exercise.getUserId())
                .setTitle(exercise.getTitle())
                .setDescription(exercise.getDescription());
    }
}
