package fr.esgi.pa.server.exercise_case.usecase;

import fr.esgi.pa.server.code.core.adapter.CodeAdapter;
import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.core.dto.DtoCode;
import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseTest;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;
import fr.esgi.pa.server.exercise_case.core.usecase.GetAllExerciseCaseByUserId;
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.adapter.ExerciseCaseAdapter;
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.adapter.ExerciseTestAdapter;
import fr.esgi.pa.server.language.core.LanguageDao;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllExerciseCaseByUserIdImpl implements GetAllExerciseCaseByUserId {
    private final CodeDao codeDao;
    private final ExerciseCaseDao exerciseCaseDao;
    private final LanguageDao languageDao;
    private final ExerciseTestDao exerciseTestDao;
    private final ExerciseCaseAdapter exerciseCaseAdapter;
    private final ExerciseTestAdapter exerciseTestAdapter;
    private final CodeAdapter codeAdapter;

    public Set<DtoExerciseCase> execute(Long userId) throws NotFoundException {
        var setCode = getAllResolvedCodeByUserId(userId);
        var setExerciseCaseId = getSetExerciseCaseId(setCode);
        var setExerciseCase = exerciseCaseDao.findAllByIdIn(setExerciseCaseId);
        var setDtoExerciseCase = new HashSet<DtoExerciseCase>();
        for (ExerciseCase exerciseCase : setExerciseCase) {
            setDtoExerciseCase.add(getDtoExerciseCase(setCode, exerciseCase));
        }

        return setDtoExerciseCase;
    }

    private Set<Code> getAllResolvedCodeByUserId(Long userId) {
        return codeDao.findAllByUserId(userId).stream()
                .filter(Code::getIsResolved)
                .collect(Collectors.toSet());
    }

    @NotNull
    private Set<Long> getSetExerciseCaseId(Set<Code> setCode) {
        return setCode.stream()
                .map(Code::getExerciseCaseId).collect(Collectors.toSet());
    }

    @NotNull
    private DtoExerciseCase getDtoExerciseCase(Set<Code> setCode, ExerciseCase exerciseCase) throws NotFoundException {
        var dtoExerciseCase = exerciseCaseAdapter.domainToDto(exerciseCase);
        var language = languageDao.findById(exerciseCase.getLanguageId());
        var setExerciseTest = exerciseTestDao.findAllByExerciseCaseId(exerciseCase.getId());
        var setDtoExerciseTest = mapSetExerciseTestToDto(setExerciseTest);
        var setDtoCode = filterCodeToExerciseCaseAndMapToDto(setCode, exerciseCase);

        dtoExerciseCase.setLanguage(language);
        dtoExerciseCase.setTests(setDtoExerciseTest);
        dtoExerciseCase.setCodes(setDtoCode);
        return dtoExerciseCase;
    }

    @NotNull
    private Set<DtoExerciseTest> mapSetExerciseTestToDto(Set<fr.esgi.pa.server.exercise_case.core.entity.ExerciseTest> setExerciseTest) {
        return setExerciseTest.stream()
                .map(exerciseTestAdapter::domainToDto)
                .collect(Collectors.toSet());
    }

    @NotNull
    private Set<DtoCode> filterCodeToExerciseCaseAndMapToDto(Set<Code> setCode, ExerciseCase exerciseCase) {
        return setCode.stream()
                .filter(code -> code.getExerciseCaseId().equals(exerciseCase.getId()))
                .map(codeAdapter::domainToDto)
                .collect(Collectors.toSet());
    }
}
