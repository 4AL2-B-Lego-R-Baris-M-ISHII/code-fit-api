package fr.esgi.pa.server.exercise.infrastructure.dataprovider.mapper;

import fr.esgi.pa.server.common.core.mapper.MapperDomainToEntity;
import fr.esgi.pa.server.common.core.mapper.MapperEntityToDomain;
import fr.esgi.pa.server.exercise.core.entity.Exercise;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExercise;
import org.springframework.stereotype.Component;

@Component
public class ExerciseMapper implements MapperEntityToDomain<JpaExercise, Exercise>, MapperDomainToEntity<Exercise, JpaExercise> {
    @Override
    public Exercise entityToDomain(JpaExercise entity) {
        return new Exercise()
                .setId(entity.getId())
                .setTitle(entity.getTitle())
                .setDescription(entity.getDescription())
                .setUserId(entity.getUserId());
    }

    @Override
    public JpaExercise domainToEntity(Exercise domain) {
        return new JpaExercise()
                .setId(domain.getId())
                .setTitle(domain.getTitle())
                .setDescription(domain.getDescription())
                .setUserId(domain.getUserId());
    }
}
