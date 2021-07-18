package fr.esgi.pa.server.code.core.dto;

import fr.esgi.pa.server.code.core.compiler.CodeResult;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.util.List;

@Data
@Accessors(chain = true)
public class DtoCode {
    private Long codeId;
    private Boolean isResolved;
    private String content;
    private List<CodeResult> listCodeResult;
    private Timestamp resolvedDate;
}
