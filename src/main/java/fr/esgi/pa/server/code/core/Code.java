package fr.esgi.pa.server.code.core;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Result {

    private TypeResult result;

    private String output;
}
