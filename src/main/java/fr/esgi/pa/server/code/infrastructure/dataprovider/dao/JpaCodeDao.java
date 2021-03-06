package fr.esgi.pa.server.code.infrastructure.dataprovider.dao;

import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.code.infrastructure.dataprovider.mapper.CodeMapper;
import fr.esgi.pa.server.code.infrastructure.dataprovider.repository.CodeRepository;
import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.common.core.utils.date.DateHelper;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JpaCodeDao implements CodeDao {
    private final CodeRepository codeRepository;
    private final CodeMapper codeMapper;
    private final DateHelper dateHelper;

    @Override
    public Code save(Code code) throws ForbiddenException {
        checkIfUserIdIsNull(code);
        checkIfExerciseCaseIdIsNull(code);

        return saveAndReturnDomainCode(code);
    }

    private void checkIfUserIdIsNull(Code code) throws ForbiddenException {
        if (code.getUserId() == null) {
            var message = String.format(
                    "%s : Code with null user id can't be saved",
                    CommonExceptionState.FORBIDDEN
            );
            throw new ForbiddenException(message);
        }
    }

    private void checkIfExerciseCaseIdIsNull(Code code) throws ForbiddenException {
        if (code.getExerciseCaseId() == null) {
            var message = String.format(
                    "%s : Code with null exercise case id can't be saved",
                    CommonExceptionState.FORBIDDEN
            );
            throw new ForbiddenException(message);
        }
    }

    private Code saveAndReturnDomainCode(Code code) {
        var codeToSave = codeRepository
                .findByUserIdAndExerciseCaseId(code.getUserId(), code.getExerciseCaseId())
                .orElse(codeMapper.domainToEntity(code));
        codeToSave.setContent(code.getContent());
        codeToSave.setIsResolved(code.getIsResolved());
        if (code.getIsResolved())
            codeToSave.setResolvedDate(dateHelper.timestampNow());
        var savedCode = codeRepository.save(codeToSave);
        return codeMapper.entityToDomain(savedCode);
    }

    @Override
    public Code findById(Long codeId) throws NotFoundException {
        var foundCode = codeRepository.findById(codeId).orElseThrow(() -> {
            var message = String.format(
                    "%s : Code with id '%d' not found",
                    CommonExceptionState.NOT_FOUND,
                    codeId
            );
            return new NotFoundException(message);
        });
        return codeMapper.entityToDomain(foundCode);
    }

    @Override
    public Set<Code> findAllByUserId(Long userId) {
        return codeRepository.findAllByUserId(userId)
                .stream()
                .map(codeMapper::entityToDomain)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<Code> findByUserIdAndExerciseCaseId(Long userId, Long exerciseCaseId) {
        return codeRepository.findByUserIdAndExerciseCaseId(userId, exerciseCaseId)
                .map(codeMapper::entityToDomain);
    }

}
