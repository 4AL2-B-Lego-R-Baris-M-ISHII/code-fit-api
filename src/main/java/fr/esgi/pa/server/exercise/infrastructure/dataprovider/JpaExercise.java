package fr.esgi.pa.server.exercise.infrastructure.dataprovider;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String solution;

    @NotBlank
    @Column(name = "language_id")
    private Long languageId;
}
