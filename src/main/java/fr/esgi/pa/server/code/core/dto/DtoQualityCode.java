package fr.esgi.pa.server.code.core.dto;

import fr.esgi.pa.server.code.core.quality.QualityCode;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DtoQualityCode {
    private DtoCode code;
    private Long exerciseCaseId;
    private QualityCode qualityCode;
}
