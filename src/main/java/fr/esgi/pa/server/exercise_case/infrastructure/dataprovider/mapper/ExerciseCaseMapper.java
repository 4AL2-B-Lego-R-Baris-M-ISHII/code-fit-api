package fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.mapper;

import fr.esgi.pa.server.common.core.mapper.MapperDomainToEntity;
import fr.esgi.pa.server.common.core.mapper.MapperEntityToDomain;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseCase;
import org.springframework.stereotype.Component;

@Component
public class ExerciseCaseMapper implements
        MapperEntityToDomain<JpaExerciseCase, ExerciseCase>,
        MapperDomainToEntity<ExerciseCase, JpaExerciseCase> {
    @Override
    public ExerciseCase entityToDomain(JpaExerciseCase entity) {
        return new ExerciseCase()
                .setId(entity.getId())
                .setSolution(entity.getSolution())
                .setStartContent(entity.getStartContent())
                .setLanguageId(entity.getLanguageId())
                .setExerciseId(entity.getExerciseId())
                .setIsValid(entity.getIsValid());
    }

    @Override
    public JpaExerciseCase domainToEntity(ExerciseCase domain) {
        return new JpaExerciseCase()
                .setId(domain.getId())
                .setSolution(domain.getSolution())
                .setExerciseId(domain.getExerciseId())
                .setStartContent(domain.getStartContent())
                .setLanguageId(domain.getLanguageId())
                .setIsValid(domain.getIsValid());
    }
}
