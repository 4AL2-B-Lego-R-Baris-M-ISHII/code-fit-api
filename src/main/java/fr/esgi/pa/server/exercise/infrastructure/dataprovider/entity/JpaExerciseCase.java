package fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity(name = "exercise_case")
@JsonIgnoreProperties({ "exercise" })
@Data
@Accessors(chain = true)
public class JpaExerciseCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String solution;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String startContent;

    @NotNull
    @Column(name = "language_id")
    private Long languageId;


    @Column(name = "exercise_id")
    private Long exerciseId;

    private Boolean isValid;
}
