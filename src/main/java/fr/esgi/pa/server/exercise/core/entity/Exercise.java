package fr.esgi.pa.server.exercise.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Exercise {
    private Long id;
    private String title;
    private String description;
    private Long userId;
}
