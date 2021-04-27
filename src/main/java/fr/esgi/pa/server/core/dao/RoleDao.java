package fr.esgi.pa.server.core.dao;

import fr.esgi.pa.server.core.model.Role;
import fr.esgi.pa.server.core.model.RoleName;

import java.util.Optional;

public interface RoleDao {
    Optional<Role> findByRoleName(RoleName roleName);
}
