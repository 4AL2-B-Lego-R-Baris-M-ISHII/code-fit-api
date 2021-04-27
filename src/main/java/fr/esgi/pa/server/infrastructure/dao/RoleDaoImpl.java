package fr.esgi.pa.server.infrastructure.dao;

import fr.esgi.pa.server.core.dao.RoleDao;
import fr.esgi.pa.server.core.model.Role;
import fr.esgi.pa.server.core.model.RoleName;
import fr.esgi.pa.server.infrastructure.dataprovider.mapper.RoleMapper;
import fr.esgi.pa.server.infrastructure.dataprovider.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleDaoImpl implements RoleDao {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public Optional<Role> findByRoleName(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .map(roleMapper::entityToDomain);
    }
}
