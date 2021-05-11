package fr.esgi.pa.server.role.core;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Role {
    private Long id;
    private RoleName name;
}
