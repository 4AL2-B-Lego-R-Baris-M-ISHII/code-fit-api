package fr.esgi.pa.server.role.infrastructure.dataprovider;

import fr.esgi.pa.server.common.mapper.MapperDomainToEntity;
import fr.esgi.pa.server.common.mapper.MapperEntityToDomain;
import fr.esgi.pa.server.role.core.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper implements MapperEntityToDomain<RoleEntity, Role>, MapperDomainToEntity<Role, RoleEntity> {

    @Override
    public Role entityToDomain(RoleEntity entity) {
        return new Role()
                .setId(entity.getId())
                .setName(entity.getName());
    }

    @Override
    public RoleEntity domainToEntity(Role domain) {
        return new RoleEntity()
                .setId(domain.getId())
                .setName(domain.getName());
    }
}
