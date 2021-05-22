package fr.esgi.pa.server.language.infrastructure.entrypoint;

import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.usecase.FindAllLanguages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static org.springframework.http.ResponseEntity.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/language")
public class LanguageController {
    private final FindAllLanguages findAllLanguages;

    @GetMapping
    public ResponseEntity<Set<Language>> findAll() {
        return ok(findAllLanguages.execute());
    }
}
