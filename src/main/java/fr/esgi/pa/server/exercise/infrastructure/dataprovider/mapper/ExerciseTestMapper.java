package fr.esgi.pa.server.exercise.infrastructure.dataprovider.mapper;

import fr.esgi.pa.server.common.core.mapper.MapperEntityToDomain;
import fr.esgi.pa.server.exercise.core.entity.ExerciseTest;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExerciseTest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ExerciseTestMapper implements MapperEntityToDomain<JpaExerciseTest, ExerciseTest> {
    @Override
    public ExerciseTest entityToDomain(JpaExerciseTest entity) {
        return new ExerciseTest()
                .setId(entity.getId())
                .setContent(entity.getContent());
    }
}
