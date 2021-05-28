package fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity(name = "exercise_case")
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
    @Column(name = "language_id")
    private Long languageId;

    @ManyToOne
    private JpaExercise exercise;

    private Boolean isValid;

    @OneToMany(mappedBy = "currentCase")
    private Set<JpaExerciseTest> tests;
}
