package fr.esgi.pa.server.exercise.infrastructure.entrypoint;

import fr.esgi.pa.server.exercise.infrastructure.entrypoint.request.SaveExerciseRequest;
import fr.esgi.pa.server.exercise.usecase.SaveOneExercise;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exercise")
public class ExerciseController {
    private final SaveOneExercise saveOneExercise;

    @GetMapping("/test")
    public ResponseEntity<?> test(@RequestAttribute("userId") String userId) {
        return ResponseEntity.ok("userId : " + userId);
    }

    @PostMapping
    public ResponseEntity<URI> saveOne(
            @RequestAttribute("userId") String userId,
            @Valid @RequestBody SaveExerciseRequest request) {
        var newExerciseId = saveOneExercise.execute(
                request.getTitle(),
                request.getDescription(),
                request.getLanguage(),
                Long.parseLong(userId)
        );
        return null;
    }
}
