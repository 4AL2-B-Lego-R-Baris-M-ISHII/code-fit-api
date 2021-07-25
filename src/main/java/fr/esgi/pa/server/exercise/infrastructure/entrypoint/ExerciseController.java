package fr.esgi.pa.server.exercise.infrastructure.entrypoint;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dto.DtoExercise;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.exercise.core.exception.IncorrectExerciseException;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.request.SaveExerciseRequest;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.request.UpdateExerciseRequest;
import fr.esgi.pa.server.exercise.usecase.*;
import fr.esgi.pa.server.exercise_case.core.usecase.GetAllExerciseCaseByUserId;
import fr.esgi.pa.server.language.core.exception.IncorrectLanguageNameException;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/exercise")
public class ExerciseController {
    private final SaveOneExercise saveOneExercise;
    private final FindOneExercise findOneExercise;
    private final FindAllExercises findAllExercises;
    private final FilterExercisesByCreator filterExercisesByCreator;
    private final AddLoggedUserCodeAllExercises addLoggedUserCodeAllExercises;
    private final UpdateOneExercise updateOneExercise;
    private final DeleteOneExercise deleteOneExercise;
    private final GetAllExerciseCaseByUserId getAllExerciseCaseByUserId;
    private final GetAllExerciseThatUserResolved getAllExerciseThatUserResolved;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<URI> saveOne(
            @ApiIgnore @RequestAttribute("userId")
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
        return created(uri)
                .header("Access-Control-Expose-Headers", "Location")
                .build();
    }

    @GetMapping("{id}")
    public ResponseEntity<DtoExercise> findOne(
            @ApiIgnore @RequestAttribute("userId")
            @Pattern(regexp = "^\\d+$", message = "id has to be an integer")
            @Min(value = 1, message = "id has to be equal or more than 1") String userId,
            @PathVariable("id")
            @Min(value = 1, message = "id has to be equal or more than 1") Long exerciseId
    ) throws NotFoundException {
        var foundExercise = findOneExercise.execute(exerciseId, Long.parseLong(userId));

        return ok(foundExercise);
    }

    @GetMapping
    public ResponseEntity<Set<DtoExercise>> findAll(
            @ApiIgnore @RequestAttribute(name = "userId")
            @Pattern(regexp = "^\\d+$", message = "id has to be an integer")
            @Min(value = 1, message = "id has to be equal or more than 1") String userId,
            @RequestParam(name = "is_creator") Optional<Boolean> isCreator,
            @RequestParam(name = "is_valid") Optional<Boolean> isValid,
            @RequestParam(name = "with_logged_user_code") Optional<Boolean> withLoggedUserCode
    ) throws NotFoundException {
        var allExercise = findAllExercises.execute();

        if (isCreator.isPresent()) {
            allExercise = filterExercisesByCreator.execute(allExercise, Long.parseLong(userId));
        }

        if (isValid.isPresent()) {
            allExercise = allExercise.stream()
                    .peek(dtoExercise -> {
                        var filteredCases =dtoExercise.getCases().stream()
                                .filter(curCase -> curCase.getIsValid().equals(isValid.get()))
                                .collect(Collectors.toSet());
                        dtoExercise.setCases(filteredCases);
                    })
                    .filter(dtoExercise -> !dtoExercise.getCases().isEmpty())
                    .collect(Collectors.toSet());;
        }

        if (withLoggedUserCode.isPresent()) {
            allExercise = addLoggedUserCodeAllExercises.execute(allExercise, Long.parseLong(userId));
        }
        return ok(allExercise);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOne(
            @ApiIgnore @RequestAttribute("userId")
            @Pattern(regexp = "^\\d+$", message = "id has to be an integer")
            @Min(value = 1, message = "id has to be equal or more than 1") String userId,
            @PathVariable("id")
            @Min(value = 1, message = "id has to be equal or more than 1") Long exerciseId,
            @Valid @RequestBody UpdateExerciseRequest request
    ) throws IncorrectExerciseException, NotFoundException, ForbiddenException {
        updateOneExercise.execute(Long.parseLong(userId), exerciseId, request.getTitle(), request.getDescription());
        return noContent().build();
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteOne(
            @ApiIgnore @RequestAttribute("userId")
            @Pattern(regexp = "^\\d+$", message = "id has to be an integer")
            @Min(value = 1, message = "id has to be equal or more than 1") String userId,
            @PathVariable("id")
            @Min(value = 1, message = "id has to be equal or more than 1") Long exerciseId
    ) throws NotFoundException {
        deleteOneExercise.execute(Long.parseLong(userId), exerciseId);
        return noContent().build();
    }

    @GetMapping("/logged-user")
    public ResponseEntity<Set<DtoExercise>> getAllByCodesOfLoggedUser(
            @ApiIgnore @RequestAttribute("userId")
            @Pattern(regexp = "^\\d+$", message = "id has to be an integer")
            @Min(value = 1, message = "id has to be equal or more than 1") String userId
    ) throws NotFoundException {
        var setDtoExerciseCaseUserResolved = getAllExerciseCaseByUserId.execute(Long.parseLong(userId));
        var setDtoExercise = getAllExerciseThatUserResolved.execute(setDtoExerciseCaseUserResolved);
        return ok(setDtoExercise);
    }
}
