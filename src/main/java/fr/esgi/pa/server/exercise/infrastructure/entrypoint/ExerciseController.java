package fr.esgi.pa.server.exercise.infrastructure.entrypoint;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exercise")
public class ExerciseController {
    @GetMapping("/test")
    public ResponseEntity<?> test(@RequestAttribute("userId") String userId) {
        return ResponseEntity.ok("userId : " + userId);
    }
}
