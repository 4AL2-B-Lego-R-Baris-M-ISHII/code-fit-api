package fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity(name = "exercise_test")
@Data
@Accessors(chain = true)
public class JpaExerciseTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "expected_output")
    private String expectedOutput;

    @ManyToOne
    private JpaExerciseCase currentCase;
}
