package fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@JsonIgnoreProperties({ "currentCase" })
@Entity(name = "exercise_test")
@Data
@Accessors(chain = true)
public class JpaExerciseTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "exercise_case_id")
    private Long exerciseCaseId;
}
