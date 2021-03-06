package fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.adapter;

import fr.esgi.pa.server.common.core.mapper.MapperDomainToDto;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;
import org.springframework.stereotype.Component;

@Component
public class ExerciseCaseAdapter implements MapperDomainToDto<ExerciseCase, DtoExerciseCase> {
    @Override
    public DtoExerciseCase domainToDto(ExerciseCase exerciseCase) {
        return new DtoExerciseCase()
                .setId(exerciseCase.getId())
                .setIsValid(exerciseCase.getIsValid())
                .setSolution(exerciseCase.getSolution())
                .setStartContent(exerciseCase.getStartContent());
    }
}
