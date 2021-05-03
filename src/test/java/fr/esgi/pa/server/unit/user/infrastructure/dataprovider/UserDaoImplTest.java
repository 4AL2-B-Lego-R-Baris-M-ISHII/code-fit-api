package fr.esgi.pa.server.unit.user.infrastructure.dataprovider;

import fr.esgi.pa.server.user.core.UserDao;
import fr.esgi.pa.server.common.exception.NotFoundException;
import fr.esgi.pa.server.role.core.Role;
import fr.esgi.pa.server.role.core.RoleName;
import fr.esgi.pa.server.user.infrastructure.dataprovider.UserDaoImpl;
import fr.esgi.pa.server.role.infrastructure.dataprovider.RoleEntity;
import fr.esgi.pa.server.user.infrastructure.dataprovider.UserEntity;
import fr.esgi.pa.server.role.infrastructure.dataprovider.RoleMapper;
import fr.esgi.pa.server.role.infrastructure.dataprovider.RoleRepository;
import fr.esgi.pa.server.user.infrastructure.dataprovider.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDaoImplTest {
    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private RoleRepository mockRoleRepository;

    @Mock
    private PasswordEncoder mockEncoder;

    private final RoleMapper roleMapper = new RoleMapper();

    private UserDaoImpl sut;
    private final String username = "user name";
    private final String email = "user@gmail.com";
    private final String password = "userPassword";

    @BeforeEach
    void setup() {
        sut = new UserDaoImpl(mockUserRepository, mockRoleRepository, roleMapper, mockEncoder);
    }

    @Nested
    class CreateUser {
        @Test
        void when_no_admin_role_founded_should_throw_exception() {
            var adminRole = new Role().setId(1L).setName(RoleName.ROLE_ADMIN);
            var userRoleEntity = new RoleEntity().setId(1L).setName(RoleName.ROLE_USER);
            when(mockRoleRepository.findAll()).thenReturn(List.of(userRoleEntity));

            assertThatThrownBy(() -> sut.createUser(username, email, password, Set.of(adminRole)))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(UserDao.class + " : Role name '" + adminRole.getName() + "' not found");
        }

        @Test
        void when_repository_not_has_role_should_throw_exception() {
            var userRole = new Role().setId(1L).setName(RoleName.ROLE_USER);
            when(mockRoleRepository.findAll()).thenReturn(List.of());

            assertThatThrownBy(() -> sut.createUser(username, email, password, Set.of(userRole)))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(UserDao.class + " : Role name '" + userRole.getName() + "' not found");
        }

        @Test
        void when_user_saved_should_return_new_user_id() throws NotFoundException {
            var userRole = new Role().setId(1L).setName(RoleName.ROLE_USER);
            var passwordEncoded = "passwordEncoded";
            var userToSave = new UserEntity()
                    .setUsername(username)
                    .setEmail(email)
                    .setPassword(passwordEncoded)
                    .setRoles(Set.of(roleMapper.domainToEntity(userRole)));
            var savedUser = new UserEntity()
                    .setId(1L);

            when(mockRoleRepository.findAll()).thenReturn(List.of(roleMapper.domainToEntity(userRole)));
            when(mockEncoder.encode(password)).thenReturn(passwordEncoded);
            when(mockUserRepository.save(userToSave)).thenReturn(savedUser);


            var result = sut.createUser(username, email, password, Set.of(userRole));
            assertThat(result).isEqualTo(1L);
        }
    }

    @Nested
    class ExistsByUsername {
        @Test
        void should_call_existsByUsername_for_userRepository() {
            sut.existsByUsername(username);
            verify(mockUserRepository, times(1)).existsByUsername(username);
        }

        @Test
        void should_return_existsByUsername_userRepository_result() {
            when(mockUserRepository.existsByUsername(username)).thenReturn(true);

            var result = sut.existsByUsername(username);

            assertThat(result).isTrue();
        }
    }

    @Nested
    class ExistsByEmail {
        @Test
        void should_call_existsByEmail_for_userRepository() {
            sut.existsByEmail(email);
            verify(mockUserRepository, times(1)).existsByEmail(email);
        }

        @Test
        void should_return_existsByEmail_userRepository_result() {
            when(mockUserRepository.existsByEmail(email)).thenReturn(true);

            var result = sut.existsByEmail(email);

            assertThat(result).isTrue();
        }
    }
}