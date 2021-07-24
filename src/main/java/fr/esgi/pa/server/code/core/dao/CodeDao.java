package fr.esgi.pa.server.code.core.dao;

import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;

import java.util.Optional;
import java.util.Set;

public interface CodeDao {
    Code save(Code code) throws ForbiddenException;

    Code findById(Long codeId) throws NotFoundException;

    Set<Code> findAllByUserId(Long userId);

    Optional<Code> findByUserIdAndExerciseCaseId(Long exerciseCaseId, Long userId);
}
