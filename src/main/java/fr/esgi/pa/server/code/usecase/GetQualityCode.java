package fr.esgi.pa.server.code.usecase;

import fr.esgi.pa.server.code.core.adapter.CodeAdapter;
import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.core.dto.CodeQualityType;
import fr.esgi.pa.server.code.core.dto.DtoQualityCode;
import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.code.core.quality.ProcessQualityCode;
import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.user.core.dao.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class GetQualityCode {
    private final UserDao userDao;
    private final CodeDao codeDao;
    private final ExerciseCaseDao exerciseCaseDao;
    private final LanguageDao languageDao;
    private final ProcessQualityCode processQualityCode;
    private final CodeAdapter codeAdapter;

    public DtoQualityCode execute(Long userId, Long codeId, Set<CodeQualityType> codeQualityTypeSet) throws NotFoundException, ForbiddenException {
        var foundCode = codeDao.findById(codeId);

        checkIfUserExistsElseThrow(userId);
        checkIfCodeCorrespondToCurrentUserElseThrow(userId, foundCode);
        checkIfCodeIsResolvedElseThrow(foundCode);

        var foundExerciseCase = exerciseCaseDao.findById(foundCode.getExerciseCaseId());
        var foundLanguage = languageDao.findById(foundExerciseCase.getLanguageId());
        var qualityCode = processQualityCode.process(
                foundCode.getContent(),
                foundLanguage,
                codeQualityTypeSet
        );
        var dtoCode = codeAdapter.domainToDto(foundCode);
        return new DtoQualityCode()
                .setCode(dtoCode)
                .setExerciseCaseId(foundExerciseCase.getId())
                .setQualityCode(qualityCode);
    }

    private void checkIfCodeCorrespondToCurrentUserElseThrow(Long userId, Code foundCode) throws ForbiddenException {
        checkIfIsTrueElseThrowForbiddenException(
                foundCode.getUserId().equals(userId),
                "%s : Current user can't get quality code of other user's code"
        );
    }

    private void checkIfCodeIsResolvedElseThrow(Code foundCode) throws ForbiddenException {
        checkIfIsTrueElseThrowForbiddenException(
                foundCode.getIsResolved(),
                "%s : Code has to be resolved to get it quality"
        );
    }

    private void checkIfIsTrueElseThrowForbiddenException(Boolean isTrue, String messageFormat) throws ForbiddenException {
        if (isTrue) {
            return;
        }
        var message = String.format(
                messageFormat,
                CommonExceptionState.FORBIDDEN
        );
        throw new ForbiddenException(message);
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
