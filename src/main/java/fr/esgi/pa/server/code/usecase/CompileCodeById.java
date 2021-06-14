package fr.esgi.pa.server.code.usecase;

import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.core.dto.DtoCode;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompileCodeById {
    private final CodeDao codeDao;

    public DtoCode execute(Long codeId) throws NotFoundException {
        codeDao.findById(codeId);
        return null;
    }
}
