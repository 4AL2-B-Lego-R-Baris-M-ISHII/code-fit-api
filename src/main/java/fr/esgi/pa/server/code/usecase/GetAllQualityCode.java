package fr.esgi.pa.server.code.usecase;

import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.core.dto.CodeQualityType;
import fr.esgi.pa.server.code.core.dto.DtoQualityCode;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllQualityCode {
    private final CodeDao codeDao;
    private final GetQualityCode getQualityCode;

    public Set<DtoQualityCode> execute(Long userId) {
        var setCode = codeDao.findAllByUserId(userId);

        var setQualityType = Set.of(
                CodeQualityType.LINES_CODE,
                CodeQualityType.LINES_COMMENT,
                CodeQualityType.CYCLOMATIC_COMPLEXITY,
                CodeQualityType.HAS_DUPLICATE_CODE
        );
        return setCode.stream()
                .map(code -> {
                    try {
                        return getQualityCode.execute(code.getUserId(), code.getId(), setQualityType);
                    } catch (NotFoundException | ForbiddenException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toSet());
    }
}
