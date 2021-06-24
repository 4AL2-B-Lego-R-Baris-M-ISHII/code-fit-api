package fr.esgi.pa.server.code.usecase;

import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.core.dto.CodeQualityType;
import fr.esgi.pa.server.code.core.dto.DtoQualityCode;
import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.user.core.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Stack;

@Service
@RequiredArgsConstructor
public class GetQualityCode {
    private final UserDao userDao;
    private final CodeDao codeDao;

    public DtoQualityCode execute(Long userId, Long codeId, Stack<CodeQualityType> codeQualityTypeSet) throws NotFoundException, ForbiddenException {
        var foundCode = codeDao.findById(codeId);

        checkIfUserExistsElseThrow(userId);
        checkIfCodeCorrespondToCurrentUserElseThrow(userId, foundCode);


        return null;
    }

    private void checkIfCodeCorrespondToCurrentUserElseThrow(Long userId, fr.esgi.pa.server.code.core.entity.Code foundCode) throws ForbiddenException {
        if (!foundCode.getUserId().equals(userId)) {
            var message = String.format(
                    "%s : Current user can't get quality code of other user's code",
                    CommonExceptionState.FORBIDDEN
            );
            throw new ForbiddenException(message);
        }
    }

    private void checkIfUserExistsElseThrow(Long userId) throws NotFoundException {
        if (!userDao.existsById(userId)) {
            var message = String.format(
                    "%s : User with id '%d' not found",
                    CommonExceptionState.NOT_FOUND,
                    userId
            );
            throw new NotFoundException(message);
        }
    }
}
