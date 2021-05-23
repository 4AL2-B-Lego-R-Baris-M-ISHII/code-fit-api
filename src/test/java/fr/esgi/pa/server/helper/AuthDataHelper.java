package fr.esgi.pa.server.helper;

import fr.esgi.pa.server.auth.infrastructure.security.service.UserDetailsImpl;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthDataHelper {
    private UserDetailsImpl user;
    private String token;
}
