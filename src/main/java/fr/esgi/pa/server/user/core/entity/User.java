package fr.esgi.pa.server.user.core.entity;

import fr.esgi.pa.server.role.core.Role;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Set<Role> roles;
}
