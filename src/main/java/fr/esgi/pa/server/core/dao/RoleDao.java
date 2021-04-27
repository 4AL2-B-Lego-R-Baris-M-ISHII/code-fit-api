package fr.esgi.pa.server.core.dao;

import fr.esgi.pa.server.core.exception.AlreadyCreatedException;
import fr.esgi.pa.server.core.model.Role;
import fr.esgi.pa.server.core.model.RoleName;

import java.util.List;
import java.util.Optional;

public interface RoleDao {
    Long createRole(RoleName roleName) throws AlreadyCreatedException;

    Optional<Role> findByRoleName(RoleName roleName);

    List<Role> findAll();
}
