package fr.esgi.pa.server.code.infrastructure.dataprovider.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity(name = "code")
@Data
@Accessors(chain = true)
public class JpaCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String content;

    @NotNull
    @Column(name = "user_id")
    private Long userId;

    @NotNull
    @Column(name = "exercise_case_id")
    private Long exerciseCaseId;

    @NotNull
    @Column(columnDefinition = "boolean default false")
    private Boolean isResolved;

    @Column(name = "resolved_date")
    private Timestamp resolvedDate;
}
