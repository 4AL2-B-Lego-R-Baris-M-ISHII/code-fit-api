package fr.esgi.pa.server.code.usecase;

import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.user.core.dao.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveOneCode {
    private final UserDao userDao;
    private final ExerciseCaseDao exerciseCaseDao;
    private final CodeDao codeDao;

    public Long execute(Long userId, Long exerciseCaseId, String codeContent) throws NotFoundException, ForbiddenException {
        checkIfUserExists(userId);
        checkIfExerciseCaseIsValid(exerciseCaseId);

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

    private void checkIfExerciseCaseIsValid(Long exerciseCaseId) throws NotFoundException, ForbiddenException {
        var exerciseCase = exerciseCaseDao.findById(exerciseCaseId);
        if (!exerciseCase.getIsValid()) {
            var message = String.format(
                    "%s : Can't save code of not valid exercise case",
                    CommonExceptionState.FORBIDDEN

            );
            throw new ForbiddenException(message);
        }
    }

    private Long savedCode(Long userId, Long exerciseCaseId, String codeContent) throws ForbiddenException {
        var code = new Code()
                .setUserId(userId)
                .setExerciseCaseId(exerciseCaseId)
                .setContent(codeContent);
        var savedCode = codeDao.save(code);
        return savedCode.getId();
    }
}
