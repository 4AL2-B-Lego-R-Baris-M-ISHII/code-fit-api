package fr.esgi.pa.server.exercise.infrastructure.dataprovider.mapper;

import fr.esgi.pa.server.common.core.mapper.MapperEntityToDomain;
import fr.esgi.pa.server.exercise.core.entity.ExerciseCase;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExerciseCase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ExerciseCaseMapper implements MapperEntityToDomain<JpaExerciseCase, ExerciseCase> {
    @Override
    public ExerciseCase entityToDomain(JpaExerciseCase entity) {
        return new ExerciseCase()
                .setId(entity.getId())
                .setSolution(entity.getSolution())
                .setLanguageId(entity.getLanguageId())
                .setIsValid(entity.getIsValid());
    }
}
