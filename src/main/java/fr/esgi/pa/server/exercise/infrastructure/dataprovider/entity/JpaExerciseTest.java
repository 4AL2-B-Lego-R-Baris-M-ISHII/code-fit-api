package fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity;

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

    @ManyToOne
    @JoinColumn(name = "exercise_case_id")
    private JpaExerciseCase currentCase;

    @Override
    public String toString() {
        return "JpaExerciseTest{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}
