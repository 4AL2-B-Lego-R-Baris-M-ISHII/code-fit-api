package fr.esgi.pa.server.exercise_case.usecase;

import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class GetAllExerciseCaseByUserId {
    private final CodeDao codeDao;
    private final ExerciseCaseDao exerciseCaseDao;

    public Set<DtoExerciseCase> execute(Long userId) {
        codeDao.findAllByUserId(userId);
        return null;
    }
}
