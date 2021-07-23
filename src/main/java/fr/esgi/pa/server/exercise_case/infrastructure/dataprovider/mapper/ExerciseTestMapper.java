package fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.mapper;

import fr.esgi.pa.server.common.core.mapper.MapperDomainToEntity;
import fr.esgi.pa.server.common.core.mapper.MapperEntityToDomain;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseTest;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseTest;
import org.springframework.stereotype.Component;

@Component
public class ExerciseTestMapper implements
        MapperEntityToDomain<JpaExerciseTest, ExerciseTest>,
        MapperDomainToEntity<ExerciseTest, JpaExerciseTest> {
    @Override
    public ExerciseTest entityToDomain(JpaExerciseTest entity) {
        return new ExerciseTest()
                .setId(entity.getId())
                .setContent(entity.getContent())
                .setExerciseCaseId(entity.getExerciseCaseId())
                .setPosition(entity.getPosition());
    }

    @Override
    public JpaExerciseTest domainToEntity(ExerciseTest domain) {
        return new JpaExerciseTest()
                .setId(domain.getId())
                .setExerciseCaseId(domain.getExerciseCaseId())
                .setContent(domain.getContent())
                .setPosition(domain.getPosition());
    }
}
