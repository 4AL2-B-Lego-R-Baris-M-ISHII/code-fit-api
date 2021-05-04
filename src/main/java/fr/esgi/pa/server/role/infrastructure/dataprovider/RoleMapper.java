package fr.esgi.pa.server.role.infrastructure.dataprovider;

import fr.esgi.pa.server.common.mapper.MapperDomainToEntity;
import fr.esgi.pa.server.common.mapper.MapperEntityToDomain;
import fr.esgi.pa.server.role.core.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper implements MapperEntityToDomain<JpaRole, Role>, MapperDomainToEntity<Role, JpaRole> {

    @Override
    public Role entityToDomain(JpaRole entity) {
        return new Role()
                .setId(entity.getId())
                .setName(entity.getName());
    }

    @Override
    public JpaRole domainToEntity(Role domain) {
        return new JpaRole()
                .setId(domain.getId())
                .setName(domain.getName());
    }
}
