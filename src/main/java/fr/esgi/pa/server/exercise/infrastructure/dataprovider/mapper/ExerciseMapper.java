package fr.esgi.pa.server.exercise.infrastructure.dataprovider.mapper;

import fr.esgi.pa.server.common.core.mapper.MapperEntityToDomain;
import fr.esgi.pa.server.exercise.core.entity.Exercise;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExercise;
import org.springframework.stereotype.Component;

@Component
public class ExerciseMapper implements MapperEntityToDomain<JpaExercise, Exercise> {
    @Override
    public Exercise entityToDomain(JpaExercise entity) {
        return new Exercise()
                .setId(entity.getId())
                .setTitle(entity.getTitle())
                .setDescription(entity.getDescription())
                .setUserId(entity.getUserId());
    }
}
