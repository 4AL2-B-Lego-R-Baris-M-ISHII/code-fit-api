package fr.esgi.pa.server.exercise.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class Exercise {
    private Long id;
    private String title;
    private String description;
    private String solution;
    private Long userId;
}
