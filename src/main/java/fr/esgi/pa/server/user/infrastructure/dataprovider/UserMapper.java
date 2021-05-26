package fr.esgi.pa.server.user.infrastructure.dataprovider;

import fr.esgi.pa.server.common.core.mapper.MapperEntityToDomain;
import fr.esgi.pa.server.role.infrastructure.dataprovider.RoleMapper;
import fr.esgi.pa.server.user.core.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper implements MapperEntityToDomain<JpaUser, User> {
    private final RoleMapper roleMapper;

    @Override
    public User entityToDomain(JpaUser entity) {
        return new User()
                .setId(entity.getId())
                .setUsername(entity.getUsername())
                .setPassword(entity.getPassword())
                .setRoles(entity.getRoles().stream()
                        .map(roleMapper::entityToDomain)
                        .collect(Collectors.toSet()));
    }
}
