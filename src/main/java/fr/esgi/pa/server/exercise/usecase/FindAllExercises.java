package fr.esgi.pa.server.exercise.usecase;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.core.dto.DtoExercise;
import fr.esgi.pa.server.exercise.core.dto.DtoExerciseCase;
import fr.esgi.pa.server.exercise.core.entity.Exercise;
import fr.esgi.pa.server.exercise.core.entity.ExerciseCase;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.adapter.ExerciseAdapter;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.adapter.ExerciseCaseAdapter;
import fr.esgi.pa.server.language.core.LanguageDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FindAllExercises {

    private final ExerciseDao exerciseDao;
    private final ExerciseCaseDao exerciseCaseDao;
    private final LanguageDao languageDao;
    private final ExerciseAdapter exerciseAdapter;
    private final ExerciseCaseAdapter exerciseCaseAdapter;

    public Set<DtoExercise> execute() throws NotFoundException {
        var exercises = exerciseDao.findAll();
        var result = new HashSet<DtoExercise>();

        for (Exercise exercise : exercises) {
            var dtoExercise = getDtoExerciseWithCases(exercise);
            result.add(dtoExercise);
        }

        return result;
    }

    private DtoExercise getDtoExerciseWithCases(Exercise exercise) throws NotFoundException {
        var foundCases = exerciseCaseDao.findAllByExerciseId(exercise.getId());

        var dtoExercise = exerciseAdapter.domainToDto(exercise);
        if (!foundCases.isEmpty()) {
            var setDtoCase = getDtoExerciseCases(foundCases);
            dtoExercise.setCases(setDtoCase);
        }
        return dtoExercise;
    }

    private HashSet<DtoExerciseCase> getDtoExerciseCases(Set<ExerciseCase> foundCases) throws NotFoundException {
        var setDtoCase = new HashSet<DtoExerciseCase>();
        for (ExerciseCase exerciseCase: foundCases) {
            var language = languageDao.findById(exerciseCase.getLanguageId());
            var dtoCase = exerciseCaseAdapter.domainToDto(exerciseCase);
            dtoCase.setLanguage(language);
            setDtoCase.add(dtoCase);
        }
        return setDtoCase;
    }
}
