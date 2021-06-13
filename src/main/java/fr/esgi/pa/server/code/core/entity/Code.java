package fr.esgi.pa.server.code.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Code {
    private Long id;
    private String content;
    private Long userId;
    private Long exerciseCaseId;
    private Boolean isResolved;
}
