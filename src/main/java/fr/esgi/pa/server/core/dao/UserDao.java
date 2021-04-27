package fr.esgi.pa.server.core.dao;

import fr.esgi.pa.server.core.exception.NotFoundException;
import fr.esgi.pa.server.core.model.Role;

import java.util.Set;

public interface UserDao {
    Long createUser(String username, String email, String password, Set<Role> roles) throws NotFoundException;

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
