package fr.esgi.pa.server.exercise.usecase;

import fr.esgi.pa.server.code.core.adapter.CodeAdapter;
import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.exercise.core.dto.DtoExercise;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddLoggedUserCodeAllExercises {
    private final CodeDao codeDao;
    private final CodeAdapter codeAdapter;
    public Set<DtoExercise> execute(Set<DtoExercise> setDtoExercise, Long loggedUserId) {

        return setDtoExercise.stream()
                .map(dtoExercise -> updateDtoExerciseWithCodeForEachCase(loggedUserId, dtoExercise))
                .collect(Collectors.toSet());
    }

    private @NotNull DtoExercise updateDtoExerciseWithCodeForEachCase(Long loggedUserId, DtoExercise dtoExercise) {
        var cases = dtoExercise.getCases();
        var casesWithCode = cases.stream()
                .peek(curCase -> {
                    var maybeCode = codeDao.findByUserIdAndExerciseCaseId(loggedUserId, curCase.getId());
                    maybeCode.ifPresent(code -> curCase.setCodes(Set.of(codeAdapter.domainToDto(code))));
                })
                .collect(Collectors.toSet());
        dtoExercise.setCases(casesWithCode);
        return dtoExercise;
    }
}
