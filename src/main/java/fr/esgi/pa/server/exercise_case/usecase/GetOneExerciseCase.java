package fr.esgi.pa.server.exercise_case.usecase;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseTest;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseTest;
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.adapter.ExerciseCaseAdapter;
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.adapter.ExerciseTestAdapter;
import fr.esgi.pa.server.language.core.LanguageDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetOneExerciseCase {
    private final ExerciseCaseDao exerciseCaseDao;
    private final LanguageDao languageDao;
    private final ExerciseTestDao exerciseTestDao;
    private final ExerciseCaseAdapter exerciseCaseAdapter;
    private final ExerciseTestAdapter exerciseTestAdapter;

    public DtoExerciseCase execute(Long exerciseCaseId) throws NotFoundException {
        var foundExerciseCase = exerciseCaseDao.findById(exerciseCaseId);
        var foundLanguage = languageDao.findById(foundExerciseCase.getLanguageId());
        var setTest = exerciseTestDao.findAllByExerciseCaseId(foundExerciseCase.getId());
        var dtoExercise = exerciseCaseAdapter.domainToDto(foundExerciseCase);

        dtoExercise.setLanguage(foundLanguage);
        dtoExercise.setTests(mapTestsToDtoExerciseTests(setTest));

        return dtoExercise;
    }

    private Set<DtoExerciseTest> mapTestsToDtoExerciseTests(Set<ExerciseTest> setTest) {
        return setTest.stream()
                .map(exerciseTestAdapter::domainToDto)
                .collect(Collectors.toSet());
    }
}
