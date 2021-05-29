package fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity(name = "exercise")
@Data
@Accessors(chain = true)
public class JpaExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 30)
    private String title;

    @NotBlank
    @Size(max = 100)
    private String description;

    @NotNull
    @Column(name = "user_id")
    private Long userId;

    @OneToMany(mappedBy = "exercise")
    @Fetch(FetchMode.SUBSELECT)
    private Set<JpaExerciseCase> cases;
}
