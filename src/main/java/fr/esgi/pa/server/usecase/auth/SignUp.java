package fr.esgi.pa.server.usecase.auth;

import fr.esgi.pa.server.core.dao.RoleDao;
import fr.esgi.pa.server.core.dao.UserDao;
import fr.esgi.pa.server.core.exception.NotFoundException;
import fr.esgi.pa.server.core.model.Role;
import fr.esgi.pa.server.core.model.RoleName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SignUp {
    private final UserDao userDao;
    private final RoleDao roleDao;

    public Long execute(String username, String email, String password, Set<String> roleSetStr) throws NotFoundException {
        Set<Role> roles = checkSetRoleStrAndMapToSetDomainRole(roleSetStr);

        return userDao.createUser(username, email, password, roles);
    }

    private Set<Role> checkSetRoleStrAndMapToSetDomainRole(Set<String> roleSetStr) throws NotFoundException {
        Set<Role> userRole = ifSetRoleStrNullGetUserRole(roleSetStr);
        if (userRole != null) return userRole;
        var mapRoles = new HashMap<String, RoleName>();
        mapRoles.put("user", RoleName.ROLE_USER);
        mapRoles.put("admin", RoleName.ROLE_ADMIN);

        checkIdSetRoleStrArRoleNames(roleSetStr, mapRoles);

        return mapSetRoleStrToSetDomainRole(roleSetStr, mapRoles);
    }

    private Set<Role> ifSetRoleStrNullGetUserRole(Set<String> roleSetStr) throws NotFoundException {
        if (roleSetStr == null){
            var userRole = roleDao.findByRoleName(RoleName.ROLE_USER);
            userRole.orElseThrow(() -> {
                var message = String.format("%s : role name '%s' not found", SignUp.class, RoleName.ROLE_USER);
                return new NotFoundException(message);
            });
            return Set.of(userRole.get());
        }
        return null;
    }

    private void checkIdSetRoleStrArRoleNames(Set<String> roleSetStr, HashMap<String, RoleName> mapRoles) throws NotFoundException {
        var notRoleName = roleSetStr.stream()
                .filter(roleStr -> !mapRoles.containsKey(roleStr))
                .findFirst();
        if (notRoleName.isPresent()) {
            throw new NotFoundException(SignUp.class + " : role name '" + notRoleName.get() + "' not found");
        }
    }

    private Set<Role> mapSetRoleStrToSetDomainRole(Set<String> roleSetStr, HashMap<String, RoleName> mapRoles) {
        return roleSetStr.stream()
                .map(mapRoles::get)
                .map(roleName -> roleDao.findByRoleName(roleName).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
