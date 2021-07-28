package fr.esgi.pa.server.code.infrastructure.entrypoint.controller;

import fr.esgi.pa.server.code.core.compiler.CodeResult;
import fr.esgi.pa.server.code.core.dto.CodeQualityType;
import fr.esgi.pa.server.code.core.dto.DtoQualityCode;
import fr.esgi.pa.server.code.core.exception.CompilationException;
import fr.esgi.pa.server.code.infrastructure.entrypoint.TestCompileCodeRequest;
import fr.esgi.pa.server.code.infrastructure.entrypoint.request.SaveCodeRequest;
import fr.esgi.pa.server.code.usecase.*;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.net.URI;
import java.util.Set;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/code")
public class CodeController {
    private final TestCompileCode testCompileCode;
    private final SaveOneCode saveOneCode;
    private final CompileCodeById compileCodeById;
    private final GetQualityCode getQualityCode;
    private final GetAllQualityCode getAllQualityCode;

    @PostMapping
    public ResponseEntity<?> saveCode(
            @ApiIgnore @RequestAttribute("userId")
            @Pattern(regexp = "^\\d+", message = "id has to be an integer")
            @Min(value = 1, message = "id has to be equal or more than 1") String userId,
            @Valid @RequestBody SaveCodeRequest request
    ) throws NotFoundException, ForbiddenException {
        var codeId = saveOneCode.execute(
                Long.parseLong(userId),
                request.getExerciseCaseId(),
                request.getCodeContent()
        );
        if (!request.getToCompile()) {
            var uri = getUri(codeId);
            return created(uri)
                    .header("Access-Control-Expose-Headers", "Location")
                    .build();
        }
        var dtoCode = compileCodeById.execute(codeId);
        return ok(dtoCode);
    }

    private URI getUri(Long codeId) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(codeId)
                .toUri();
    }

    @PostMapping("test")
    public ResponseEntity<CodeResult> testCompileCode(@Valid @RequestBody TestCompileCodeRequest testCompileCodeRequest) throws NotFoundException, CompilationException {
        return ok(testCompileCode.execute(testCompileCodeRequest.getContent(), testCompileCodeRequest.getLanguage()));
    }

    @GetMapping("{id}/code-quality")
    public ResponseEntity<DtoQualityCode> getQualityCode(
            @ApiIgnore @RequestAttribute("userId")
            @Pattern(regexp = "^\\d+", message = "id has to be an integer")
            @Min(value = 1, message = "id has to be equal or more than 1") String userId,
            @PathVariable("id")
            @Min(value = 1, message = "id has to be equal or more than 1") Long codeId,
            @RequestParam(value = "type") Set<CodeQualityType> codeQualityTypeSet
    ) throws NotFoundException, ForbiddenException {
        var dtoQualityCode = getQualityCode.execute(Long.parseLong(userId), codeId, codeQualityTypeSet);
        return ok(dtoQualityCode);
    }

    @GetMapping("/code-quality")
    public ResponseEntity<Set<DtoQualityCode>> getAllQualityCode(
            @ApiIgnore @RequestAttribute("userId")
            @Pattern(regexp = "^\\d+", message = "id has to be an integer")
            @Min(value = 1, message = "id has to be equal or more than 1") String userId
    ) {
        var setDtoQualityCode = getAllQualityCode.execute(Long.parseLong(userId));
        return ok(setDtoQualityCode);
    }
}
