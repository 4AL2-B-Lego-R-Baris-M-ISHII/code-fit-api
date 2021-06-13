package fr.esgi.pa.server.code.usecase;

import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.user.core.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveOneCode {
    private final UserDao userDao;
    private final ExerciseCaseDao exerciseCaseDao;
    private final CodeDao codeDao;

    public Long execute(Long userId, Long exerciseCaseId, String codeContent) throws NotFoundException {
        checkIfUserExists(userId);
        checkIfExerciseCaseExists(exerciseCaseId);

        return savedCode(userId, exerciseCaseId, codeContent);
    }

    private void checkIfUserExists(Long userId) throws NotFoundException {
        if (!userDao.existsById(userId)) {
            var message = String.format(
                    "%s : User with id '%d' not found",
                    CommonExceptionState.NOT_FOUND,
                    userId
            );
            throw new NotFoundException(message);
        }
    }

    private void checkIfExerciseCaseExists(Long exerciseCaseId) throws NotFoundException {
        if (!exerciseCaseDao.existsById(exerciseCaseId)) {
            var message = String.format(
                    "%s : Exercise case with id '%d' not found",
                    CommonExceptionState.NOT_FOUND,
                    exerciseCaseId
            );
            throw new NotFoundException(message);
        }
    }

    private Long savedCode(Long userId, Long exerciseCaseId, String codeContent) {
        var code = new Code()
                .setUserId(userId)
                .setExerciseCaseId(exerciseCaseId)
                .setContent(codeContent);
        var savedCode = codeDao.save(code);
        return savedCode.getId();
    }
}
