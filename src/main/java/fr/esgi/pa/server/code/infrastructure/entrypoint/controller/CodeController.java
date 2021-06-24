package fr.esgi.pa.server.code.infrastructure.entrypoint.controller;

import fr.esgi.pa.server.code.core.compiler.CodeResult;
import fr.esgi.pa.server.code.core.exception.CompilationException;
import fr.esgi.pa.server.code.infrastructure.entrypoint.TestCompileCodeRequest;
import fr.esgi.pa.server.code.infrastructure.entrypoint.request.SaveCodeRequest;
import fr.esgi.pa.server.code.usecase.CompileCodeById;
import fr.esgi.pa.server.code.usecase.SaveOneCode;
import fr.esgi.pa.server.code.usecase.TestCompileCode;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.net.URI;

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

    @PostMapping
    public ResponseEntity<?> saveCode(
            @RequestAttribute("userId")
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
            return created(getUri(codeId)).build();
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
    public ResponseEntity<?> getQualityCode(
            @RequestAttribute("userId")
            @Pattern(regexp = "^\\d+", message = "id has to be an integer")
            @Min(value = 1, message = "id has to be equal or more than 1") String userId,
            @PathVariable("id")
            @Min(value = 1, message = "id has to be equal or more than 1") Long codeId
    ) throws NotFoundException {
        return null;
    }
}
