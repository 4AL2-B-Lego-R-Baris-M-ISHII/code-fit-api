package fr.esgi.pa.server.language.infrastructure.dataprovider;

import fr.esgi.pa.server.language.core.LanguageName;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity(name = "language")
@Data
@Accessors(chain = true)
public class JpaLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private LanguageName name;

    private String fileExtension;
}
