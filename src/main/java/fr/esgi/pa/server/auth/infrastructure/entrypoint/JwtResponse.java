package fr.esgi.pa.server.auth.infrastructure.entrypoint;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class JwtResponse {
    private Long id;
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private List<String> roles;
}
