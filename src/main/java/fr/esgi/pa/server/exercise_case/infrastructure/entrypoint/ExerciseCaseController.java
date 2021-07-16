package fr.esgi.pa.server.exercise_case.infrastructure.entrypoint;

import fr.esgi.pa.server.common.core.exception.AlreadyCreatedException;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.adapter.ExerciseTestAdapter;
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.request.SaveExerciseCaseRequest;
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.request.UpdateExerciseCaseRequest;
import fr.esgi.pa.server.exercise_case.usecase.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/exercise-case")
public class ExerciseCaseController {
    private final CreateExerciseCase createExerciseCase;
    private final UpdateExerciseCase updateExerciseCase;
    private final VerifyExerciseCase verifyExerciseCase;
    private final GetOneExerciseCase getOneExerciseCase;
    private final DeleteOneExerciseCase deleteOneExerciseCase;
    private final ExerciseTestAdapter exerciseTestAdapter;
    private final GetAllExerciseCaseByUserId getAllExerciseCaseByUserId;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<URI> createOne(
            @ApiIgnore @RequestAttribute("userId")
            @Pattern(regexp = "^\\d+$", message = "id has to be an integer")
            @Min(value = 1, message = "id has to be equal or more than 1") String userId,
            @Valid @RequestBody SaveExerciseCaseRequest request
    ) throws NotFoundException, ForbiddenException, AlreadyCreatedException {
        var newExerciseCaseId = createExerciseCase.execute(
                Long.parseLong(userId),
                request.getExerciseId(),
                request.getLanguageId()
        );

        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newExerciseCaseId)
                .toUri();
        return created(uri)
                .header("Access-Control-Expose-Headers", "Location")
                .build();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOne(
            @ApiIgnore @RequestAttribute("userId")
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

    @GetMapping("{id}")
    public ResponseEntity<DtoExerciseCase> getOneById(
            @ApiIgnore @RequestAttribute("userId")
            @Pattern(regexp = "^\\d+$", message = "id has to be an integer")
            @Min(value = 1, message = "id has to be equal or more than 1") String userId,
            @PathVariable("id")
            @Min(value = 1, message = "id has to be equal or more than 1") Long exerciseCaseId
    ) throws NotFoundException, ForbiddenException {
        var dtoExerciseCase = getOneExerciseCase.execute(Long.valueOf(userId), exerciseCaseId);
        return ok(dtoExerciseCase);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteOneById(
            @PathVariable("id")
            @Min(value = 1, message = "id has to be equal or more than 1") Long exerciseCaseId
    ) throws NotFoundException {
        deleteOneExerciseCase.execute(exerciseCaseId);
        return noContent().build();
    }

    @GetMapping("/logged-user")
    public ResponseEntity<Set<DtoExerciseCase>> getAllByLoggedUserId(
            @ApiIgnore @RequestAttribute("userId")
            @Pattern(regexp = "^\\d+$", message = "id has to be an integer")
            @Min(value = 1, message = "id has to be equal or more than 1") String userId
    ) {
        getAllExerciseCaseByUserId.execute(Long.parseLong(userId));
        return null;
    }
}
