package fr.esgi.pa.server.role.infrastructure.dataprovider;

import fr.esgi.pa.server.role.core.RoleName;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity(name = "role")
@Data
@Accessors(chain = true)
public class JpaRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoleName name;
}
