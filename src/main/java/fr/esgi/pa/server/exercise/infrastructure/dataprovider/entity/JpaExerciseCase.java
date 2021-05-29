package fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private JpaExercise exercise;

    private Boolean isValid;

    @OneToMany(mappedBy = "currentCase")
    @Fetch(FetchMode.SUBSELECT)
    private List<JpaExerciseTest> tests;

    @Override
    public String toString() {
        return "JpaExerciseCase{" +
                "id=" + id +
                ", solution='" + solution + '\'' +
                ", startContent='" + startContent + '\'' +
                ", languageId=" + languageId +
                ", isValid=" + isValid +
                ", tests=" + tests +
                '}';
    }
}
