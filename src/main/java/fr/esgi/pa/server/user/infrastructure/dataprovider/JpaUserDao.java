package fr.esgi.pa.server.user.infrastructure.dataprovider;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.role.core.Role;
import fr.esgi.pa.server.role.infrastructure.dataprovider.RoleMapper;
import fr.esgi.pa.server.role.infrastructure.dataprovider.RoleRepository;
import fr.esgi.pa.server.user.core.User;
import fr.esgi.pa.server.user.core.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JpaUserDao implements UserDao {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;

    @Transactional
    @Override
    public Long createUser(String username, String email, String password, Set<Role> roles) throws NotFoundException {
        var foundRoles = getAllRoles();
        checkFoundRolesContainSetRolesElseThrow(foundRoles, roles);

        return saveUserAndReturnId(username, email, password, roles);
    }

    private Long saveUserAndReturnId(String username, String email, String password, Set<Role> roles) {
        var userToSave = new JpaUser()
                .setUsername(username)
                .setEmail(email)
                .setPassword(encoder.encode(password))
                .setRoles(roles.stream().map(roleMapper::domainToEntity).collect(Collectors.toSet()));

        return userRepository.save(userToSave).getId();
    }

    private void checkFoundRolesContainSetRolesElseThrow(List<Role> foundRoles, Set<Role> roles) throws NotFoundException {
        var maybeNotFoundRole = roles.stream()
                .filter(role -> !foundRoles.contains(role))
                .findFirst();

        if (maybeNotFoundRole.isPresent()) {
            var message = String.format("%s : Role name '%s' not found",
                    UserDao.class,
                    maybeNotFoundRole.get().getName()
            );
            throw new NotFoundException(message);
        }
    }

    private List<Role> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User findById(Long userId) throws NotFoundException {
        var foundUser = userRepository.findById(userId).orElseThrow(() -> {
            var message = String.format("%s : User with user id '%d' not found", this.getClass(), userId);
            return new NotFoundException(message);
        });
        return userMapper.entityToDomain(foundUser);
    }

    @Override
    public Boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }
}
