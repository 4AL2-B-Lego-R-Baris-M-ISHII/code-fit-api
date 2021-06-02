package fr.esgi.pa.server.exercise.infrastructure.entrypoint;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.entity.Exercise;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.request.SaveExerciseRequest;
import fr.esgi.pa.server.exercise.usecase.FindOneExercise;
import fr.esgi.pa.server.exercise.usecase.SaveOneExercise;
import fr.esgi.pa.server.language.core.exception.IncorrectLanguageNameException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.net.URI;

import static org.springframework.http.ResponseEntity.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/exercise")
public class ExerciseController {
    private final SaveOneExercise saveOneExercise;
    private final FindOneExercise findOneExercise;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<URI> saveOne(
            @RequestAttribute("userId")
            @Pattern(regexp = "^\\d+$", message = "id has to be an integer")
            @Min(value = 1, message = "id has to be equal or more than 1") String userId,
            @Valid @RequestBody SaveExerciseRequest request) throws NotFoundException, IncorrectLanguageNameException {
        var newExerciseId = saveOneExercise.execute(
                request.getTitle(),
                request.getDescription(),
                request.getLanguage(),
                Long.parseLong(userId)
        );
        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newExerciseId)
                .toUri();
        return created(uri).build();
    }

    @GetMapping("{id}")
    public ResponseEntity<Exercise> findOne(
            @RequestAttribute("userId")
            @Pattern(regexp = "^\\d+$", message = "id has to be an integer")
            @Min(value = 1, message = "id has to be equal or more than 1") String userId,
            @PathVariable("id")
            @Min(value = 1, message = "id has to be equal or more than 1") Long exerciseId
    ) {
        findOneExercise.execute(exerciseId, Long.parseLong(userId));

        return ResponseEntity.ok(null);
    }
}
