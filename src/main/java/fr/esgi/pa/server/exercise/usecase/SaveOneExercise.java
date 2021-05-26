package fr.esgi.pa.server.exercise.usecase;

import org.springframework.stereotype.Service;

@Service
public class SaveOneExercise {
    public Long execute(
            String title,
            String description,
            String language,
            Long userId) {
        // TODO : check if language exist, if not exist throw

        // TODO : create exercise
        return null;
    }
}
