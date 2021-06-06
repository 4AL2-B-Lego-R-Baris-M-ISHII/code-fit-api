package fr.esgi.pa.server.exercise.infrastructure.exception;

import fr.esgi.pa.server.exercise.core.exception.ForbiddenSaveExerciseException;
import fr.esgi.pa.server.language.core.exception.IncorrectLanguageNameException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExerciseExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(IncorrectLanguageNameException.class)
    public ResponseEntity<String> on(IncorrectLanguageNameException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenSaveExerciseException.class)
    public ResponseEntity<String> on(ForbiddenSaveExerciseException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }
}
