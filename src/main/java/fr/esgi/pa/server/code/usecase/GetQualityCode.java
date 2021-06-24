package fr.esgi.pa.server.code.usecase;

import fr.esgi.pa.server.code.core.dto.DtoQualityCode;
import fr.esgi.pa.server.code.core.dto.QualityCodeType;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class GetQualityCode {
    public DtoQualityCode execute(Long userId, Long codeId, Set<QualityCodeType> qualityCodeTypeSet) {
        return null;
    }
}
