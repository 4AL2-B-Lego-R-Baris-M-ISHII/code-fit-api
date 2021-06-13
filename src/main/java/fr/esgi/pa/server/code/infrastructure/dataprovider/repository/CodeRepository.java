package fr.esgi.pa.server.code.infrastructure.dataprovider.repository;

import fr.esgi.pa.server.code.infrastructure.dataprovider.entity.JpaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeRepository extends JpaRepository<JpaCode, Long> {
}
