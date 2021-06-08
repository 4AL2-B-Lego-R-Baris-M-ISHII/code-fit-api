package fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.adapter;

import fr.esgi.pa.server.common.core.mapper.MapperDomainToDto;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseTest;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseTest;
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.request.UpdateExerciseTestRequest;
import org.springframework.stereotype.Component;

@Component
public class ExerciseTestAdapter implements
        MapperDomainToDto<ExerciseTest, DtoExerciseTest> {
    @Override
    public DtoExerciseTest domainToDto(ExerciseTest exerciseTest) {
        return new DtoExerciseTest()
                .setId(exerciseTest.getId())
                .setContent(exerciseTest.getContent());
    }

    public ExerciseTest requestToDomain(UpdateExerciseTestRequest request, Long exerciseCaseId) {
        return new ExerciseTest()
                .setId(request.getId())
                .setContent(request.getContent())
                .setExerciseCaseId(exerciseCaseId);
    }
}
