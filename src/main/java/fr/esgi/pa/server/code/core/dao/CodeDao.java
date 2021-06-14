package fr.esgi.pa.server.code.core.dao;

import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;

public interface CodeDao {
    Code save(Code code) throws ForbiddenException;

    Code findById(Long codeId) throws NotFoundException;
}
