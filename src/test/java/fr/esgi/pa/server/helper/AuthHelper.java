package fr.esgi.pa.server.helper;

import fr.esgi.pa.server.auth.infrastructure.security.jwt.JwtUtils;
import fr.esgi.pa.server.common.exception.NotFoundException;
import fr.esgi.pa.server.role.core.Role;
import fr.esgi.pa.server.user.core.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthHelper {
    private final UserDao userDao;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public String createUserAndGetJwt(
            String username,
            String email,
            String password,
            Set<Role> roles
    ) throws NotFoundException {
        userDao.createUser(username, email, password, roles);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateJwtToken(authentication);
    }
}
