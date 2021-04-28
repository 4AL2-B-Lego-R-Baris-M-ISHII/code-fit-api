package fr.esgi.pa.server.infrastructure.entrypoint.controller;

import fr.esgi.pa.server.core.exception.NotFoundException;
import fr.esgi.pa.server.infrastructure.dataprovider.repository.RoleRepository;
import fr.esgi.pa.server.infrastructure.dataprovider.repository.UserRepository;
import fr.esgi.pa.server.infrastructure.entrypoint.request.LoginRequest;
import fr.esgi.pa.server.infrastructure.entrypoint.request.SignUpRequest;
import fr.esgi.pa.server.infrastructure.entrypoint.response.JwtResponse;
import fr.esgi.pa.server.infrastructure.entrypoint.response.MessageResponse;
import fr.esgi.pa.server.infrastructure.security.jwt.JwtUtils;
import fr.esgi.pa.server.infrastructure.security.service.UserDetailsImpl;
import fr.esgi.pa.server.usecase.auth.SignUp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
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
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signupRequest) throws NotFoundException {
//        if (userRepository.existsByUsername(signupRequest.getUsername())) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("Error: Username is already taken"));
//        }
//        if (userRepository.existsByEmail(signupRequest.getEmail())) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("Error: Email is already in use!"));
//        }
//        UserEntity userEntity = new UserEntity()
//                .setUsername(signupRequest.getUsername())
//                .setEmail(signupRequest.getEmail())
//                .setPassword(encoder.encode(signupRequest.getPassword()));
//        Set<String> strRoles = signupRequest.getRoles();
//        Set<RoleEntity> roles = new HashSet<>();
//
//        if (strRoles == null) {
//            RoleEntity userRole = roleRepository.findByName(RoleName.ROLE_USER)
//                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//            roles.add(userRole);
//        } else {
//            strRoles.forEach(role -> {
//                if ("admin".equals(role)) {
//                    RoleEntity adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
//                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                    roles.add(adminRole);
//                } else {
//                    RoleEntity userRole = roleRepository.findByName(RoleName.ROLE_USER)
//                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                    roles.add(userRole);
//                }
//            });
//        }
//        userEntity.setRoles(roles);
//        userRepository.save(userEntity);

        signUp.execute(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getPassword(),
                signupRequest.getRoles()
        );

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
