package fr.esgi.pa.server.code.core.dao;

import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;

public interface CodeDao {
    Code save(Code code) throws ForbiddenException;
}
