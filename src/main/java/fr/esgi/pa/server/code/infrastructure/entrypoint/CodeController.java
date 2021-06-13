package fr.esgi.pa.server.code.infrastructure.entrypoint;

import fr.esgi.pa.server.code.core.compiler.CodeResult;
import fr.esgi.pa.server.code.core.exception.CompilationException;
import fr.esgi.pa.server.code.infrastructure.entrypoint.request.SaveCodeRequest;
import fr.esgi.pa.server.code.usecase.SaveOneCode;
import fr.esgi.pa.server.code.usecase.TestCompileCode;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/code")
public class CodeController {
    private final TestCompileCode testCompileCode;
    private final SaveOneCode saveOneCode;

    @PostMapping
    public ResponseEntity<?> saveCode(
            @RequestAttribute("userId")
            @Pattern(regexp = "^\\d+", message = "id has to be an integer")
            @Min(value = 1, message = "id has to be equal or more than 1") String userId,
            @Valid @RequestBody SaveCodeRequest request
    ) {
        saveOneCode.execute(Long.parseLong(userId), request.getExerciseCaseId(), request.getCodeContent());
        return null;
    }

    @PostMapping("test")
    public ResponseEntity<CodeResult> testCompileCode(@Valid @RequestBody TestCompileCodeRequest testCompileCodeRequest) throws NotFoundException, CompilationException {
        return ok(testCompileCode.execute(testCompileCodeRequest.getContent(), testCompileCodeRequest.getLanguage()));
    }
}
