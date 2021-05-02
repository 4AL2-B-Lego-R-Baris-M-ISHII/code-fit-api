package fr.esgi.pa.server.infrastructure.dataprovider.entity;

import fr.esgi.pa.server.core.model.RoleName;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity(name = "role")
@Data
@Accessors(chain = true)
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoleName name;
}
