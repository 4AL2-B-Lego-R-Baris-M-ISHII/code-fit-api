package fr.esgi.pa.server.exercise_case.usecase;

import fr.esgi.pa.server.code.core.adapter.CodeAdapter;
import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PrepareExerciseCaseForUser {
    private final CodeDao codeDao;
    private final CodeAdapter codeAdapter;

    public DtoExerciseCase execute(Long userId, DtoExerciseCase dtoExerciseCase, Boolean notAdminPart) {
        if (!notAdminPart) {
            return dtoExerciseCase;
        }
        dtoExerciseCase.setTests(null);
        var maybeDtoCode = codeDao.findByUserIdAndExerciseCaseId(userId, dtoExerciseCase.getId())
                .map(codeAdapter::domainToDto);
        maybeDtoCode.ifPresent(dtoCode -> dtoExerciseCase.setCodes(Set.of(dtoCode)));
        return dtoExerciseCase;
    }
}
