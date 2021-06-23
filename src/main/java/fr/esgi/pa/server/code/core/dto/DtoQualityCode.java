package fr.esgi.pa.server.code.core.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class DtoQualityCode {
    private Long codeId;
    private Long exerciseCaseId;
    private Map<QualityCodeType, Object> result;
}
