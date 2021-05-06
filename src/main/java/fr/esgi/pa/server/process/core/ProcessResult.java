package fr.esgi.pa.server.process.core;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProcessResult {
    private Integer status;
    private String out;
}
