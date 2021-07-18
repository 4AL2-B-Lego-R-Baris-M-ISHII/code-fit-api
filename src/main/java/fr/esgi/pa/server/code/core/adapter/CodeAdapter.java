package fr.esgi.pa.server.code.core.adapter;

import fr.esgi.pa.server.code.core.dto.DtoCode;
import fr.esgi.pa.server.code.core.entity.Code;

public interface CodeAdapter {
    DtoCode domainToDto(Code code);
}
