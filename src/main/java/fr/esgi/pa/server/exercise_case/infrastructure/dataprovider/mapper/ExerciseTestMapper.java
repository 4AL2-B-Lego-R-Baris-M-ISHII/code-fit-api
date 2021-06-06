package fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.mapper;

import fr.esgi.pa.server.common.core.mapper.MapperEntityToDomain;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseTest;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseTest;
import org.springframework.stereotype.Component;

@Component
public class ExerciseTestMapper implements MapperEntityToDomain<JpaExerciseTest, ExerciseTest> {
    @Override
    public ExerciseTest entityToDomain(JpaExerciseTest entity) {
        return new ExerciseTest()
                .setId(entity.getId())
                .setContent(entity.getContent())
                .setExerciseCaseId(entity.getExerciseCaseId());
    }
}
