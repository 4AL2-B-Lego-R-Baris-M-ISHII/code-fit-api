package fr.esgi.pa.server.auth.infrastructure.entrypoint;

import fr.esgi.pa.server.common.core.exception.AlreadyCreatedException;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.auth.infrastructure.security.jwt.JwtUtils;
import fr.esgi.pa.server.auth.infrastructure.security.service.UserDetailsImpl;
import fr.esgi.pa.server.auth.usecase.SignUp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final SignUp signUp;

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        var jwtResponse = new JwtResponse()
                .setToken(jwt)
                .setId(userDetails.getId())
                .setUsername(userDetails.getUsername())
                .setEmail(userDetails.getEmail())
                .setRoles(roles);
        return new ResponseEntity<>(jwtResponse, HttpStatus.CREATED);
    }

    @PostMapping("/signup")
    public ResponseEntity<HttpStatus> registerUser(@Valid @RequestBody SignUpRequest signupRequest) throws NotFoundException, AlreadyCreatedException {
        signUp.execute(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getPassword(),
                signupRequest.getRoles()
        );

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
