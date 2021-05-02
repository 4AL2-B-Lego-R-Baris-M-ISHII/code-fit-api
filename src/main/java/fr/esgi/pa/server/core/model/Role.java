package fr.esgi.pa.server.core.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Role {
    private Long id;
    private RoleName name;
}
