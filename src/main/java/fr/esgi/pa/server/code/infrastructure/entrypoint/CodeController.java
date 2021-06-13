package fr.esgi.pa.server.code.infrastructure.entrypoint;

import fr.esgi.pa.server.code.core.compiler.CodeResult;
import fr.esgi.pa.server.code.core.exception.CompilationException;
import fr.esgi.pa.server.code.usecase.TestCompileCode;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/code")
public class CodeController {
    private final TestCompileCode testCompileCode;

    @PostMapping
    public ResponseEntity<CodeResult> testCompileCode(@Valid @RequestBody TestCompileCodeRequest testCompileCodeRequest) throws NotFoundException, CompilationException {
        return ok(testCompileCode.execute(testCompileCodeRequest.getContent(), testCompileCodeRequest.getLanguage()));
    }
}
