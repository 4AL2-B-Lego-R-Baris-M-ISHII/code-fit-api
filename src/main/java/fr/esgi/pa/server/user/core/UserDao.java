package fr.esgi.pa.server.user.core;

import fr.esgi.pa.server.common.exception.NotFoundException;
import fr.esgi.pa.server.role.core.Role;

import java.util.Set;

public interface UserDao {
    Long createUser(String username, String email, String password, Set<Role> roles) throws NotFoundException;

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
