package fr.esgi.pa.server.exercise_case.infrastructure.entrypoint;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.adapter.ExerciseTestAdapter;
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.request.UpdateExerciseCaseRequest;
import fr.esgi.pa.server.exercise_case.usecase.UpdateExerciseCase;
import fr.esgi.pa.server.exercise_case.usecase.VerifyExerciseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/exercise-case")
public class ExerciseCaseController {
    private final UpdateExerciseCase updateExerciseCase;
    private final VerifyExerciseCase verifyExerciseCase;
    private final ExerciseTestAdapter exerciseTestAdapter;

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOne(
            @RequestAttribute("userId")
            @Pattern(regexp = "^\\d+$", message = "id has to be an integer")
            @Min(value = 1, message = "id has to be equal or more than 1") String userId,
            @PathVariable("id")
            @Min(value = 1, message = "id has to be equal or more than 1") Long exerciseCaseId,
            @Valid @RequestBody UpdateExerciseCaseRequest request
    ) throws NotFoundException, ForbiddenException {
        var setTest = request.getTests().stream()
                .map(requestTest -> exerciseTestAdapter.requestToDomain(requestTest, exerciseCaseId))
                .collect(Collectors.toSet());

        updateExerciseCase.execute(
                Long.parseLong(userId),
                exerciseCaseId,
                request.getSolution(),
                request.getStartContent(),
                setTest
        );
        if (!request.getVerifyCode()) {
            return noContent().build();
        }

        var result = verifyExerciseCase.execute(exerciseCaseId);
        return ok(result);
    }
}
