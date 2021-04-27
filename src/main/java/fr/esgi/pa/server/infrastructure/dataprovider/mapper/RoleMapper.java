package fr.esgi.pa.server.infrastructure.dataprovider.mapper;

import fr.esgi.pa.server.core.model.Role;
import fr.esgi.pa.server.infrastructure.dataprovider.entity.RoleEntity;
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
