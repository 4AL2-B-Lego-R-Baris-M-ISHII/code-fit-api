package fr.esgi.pa.server.code.infrastructure.entrypoint.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.net.URI;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/code-quality")
public class CodeQualityController {
    @PostMapping
    public ResponseEntity<URI> getQualityCode(
            @ApiIgnore @RequestAttribute("userId")
            @Pattern(regexp = "^\\d+", message = "id has to be an integer")
            @Min(value = 1, message = "id has to be equal or more than 1") String userId
    ) {
        return null;
    }
}
