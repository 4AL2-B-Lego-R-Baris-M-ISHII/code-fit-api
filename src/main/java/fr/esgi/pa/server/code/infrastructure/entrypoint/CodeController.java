package fr.esgi.pa.server.code.infrastructure.entrypoint;

import fr.esgi.pa.server.code.core.Code;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/code")
public class CodeController {

    @PostMapping
    public ResponseEntity<Code> compileCode(@Valid @RequestBody CodeRequest codeRequest) {
        return null;
    }
}
