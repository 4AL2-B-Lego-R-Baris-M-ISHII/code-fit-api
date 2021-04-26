package fr.esgi.pa.server.repository;

import fr.esgi.pa.server.entity.RoleEntity;
import fr.esgi.pa.server.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(RoleName name);
}