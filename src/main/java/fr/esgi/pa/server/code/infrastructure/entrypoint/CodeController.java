package fr.esgi.pa.server.code.infrastructure.entrypoint;

import fr.esgi.pa.server.code.core.Code;
import fr.esgi.pa.server.code.core.CompilationException;
import fr.esgi.pa.server.code.usecase.CompileCode;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/code")
public class CodeController {
    private final CompileCode compileCode;

    @PostMapping
    public ResponseEntity<Code> compileCode(@Valid @RequestBody CodeRequest codeRequest) throws NotFoundException, CompilationException {
        return ok(compileCode.execute(codeRequest.getContent(), codeRequest.getLanguage()));
    }
}
