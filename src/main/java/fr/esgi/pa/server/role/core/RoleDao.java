package fr.esgi.pa.server.role.core;

import fr.esgi.pa.server.common.exception.AlreadyCreatedException;

import java.util.List;
import java.util.Optional;

public interface RoleDao {
    Long createRole(RoleName roleName) throws AlreadyCreatedException;

    Optional<Role> findByRoleName(RoleName roleName);

    List<Role> findAll();
}
