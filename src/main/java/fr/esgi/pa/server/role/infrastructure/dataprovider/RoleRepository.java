package fr.esgi.pa.server.role.infrastructure.dataprovider;

import fr.esgi.pa.server.role.infrastructure.dataprovider.RoleEntity;
import fr.esgi.pa.server.role.core.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(RoleName name);
}