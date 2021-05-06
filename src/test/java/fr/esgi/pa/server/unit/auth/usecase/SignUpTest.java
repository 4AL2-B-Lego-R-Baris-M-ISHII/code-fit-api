package fr.esgi.pa.server.unit.auth.usecase;

import fr.esgi.pa.server.role.core.RoleDao;
import fr.esgi.pa.server.user.core.UserDao;
import fr.esgi.pa.server.common.core.exception.AlreadyCreatedException;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.role.core.Role;
import fr.esgi.pa.server.role.core.RoleName;
import fr.esgi.pa.server.auth.usecase.SignUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpTest {
    private final String username = "user name";
    private final String email = "user@gmail.com";
    private final String userPassword = "userpassword";

    @Mock
    private UserDao mockUserDao;

    @Mock
    private RoleDao mockRoleDao;

    private SignUp sut;


    @BeforeEach
    void setup() {
        sut = new SignUp(mockUserDao, mockRoleDao);
    }

    @Test
    void when_username_already_exists_should_throw_AlreadyCreatedException() {
        when(mockUserDao.existsByUsername(username)).thenReturn(true);

        assertThatThrownBy(() -> sut.execute(username, email, userPassword, null))
                .isExactlyInstanceOf(AlreadyCreatedException.class)
                .hasMessage(SignUp.class + " : user with username '" + username + "' already created");
    }

    @Test
    void when_email_already_exists_should_throw_AlreadyCreatedException() {
        when(mockUserDao.existsByEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> sut.execute(username, email, userPassword, null))
                .isExactlyInstanceOf(AlreadyCreatedException.class)
                .hasMessage(SignUp.class + " : user with email '" + email + "' already created");
    }


    @Test
    void when_setRoleStr_null_should_create_user_with_user_role() throws NotFoundException, AlreadyCreatedException {
        var userRole = new Role().setId(1L).setName(RoleName.ROLE_USER);
        when(mockRoleDao.findByRoleName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));

        sut.execute(username, email, userPassword, null);
        verify(mockUserDao, times(1))
                .createUser(username, email, userPassword, Set.of(userRole));
    }

    @Test
    void when_roleSetStr_contain_other_role_should_throw_exception() {
        assertThatThrownBy(() -> sut.execute(username, email, userPassword, Set.of("other_role")))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage(SignUp.class + " : role name 'other_role' not found");
    }

    @Test
    void when_roleSetStr_contain_user_role_should_create_user_with_user_role() throws NotFoundException, AlreadyCreatedException {
        var userRole = new Role().setId(1L).setName(RoleName.ROLE_USER);
        when(mockRoleDao.findByRoleName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));

        sut.execute(username, email, userPassword, Set.of("user"));
        verify(mockUserDao, times(1)).createUser(username, email, userPassword, Set.of(userRole));
    }

    @Test
    void when_roleSetStr_contain_admin_role_should_create_user_with_admin_role() throws NotFoundException, AlreadyCreatedException {
        var adminRole = new Role().setId(2L).setName(RoleName.ROLE_ADMIN);
        when(mockRoleDao.findByRoleName(RoleName.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));

        sut.execute(username, email, userPassword, Set.of("admin"));
        verify(mockUserDao, times(1)).createUser(username, email, userPassword, Set.of(adminRole));
    }

    @Test
    void when_roleSetStr_contain_user_and_admin_roles_should_create_user_with_user_and_admin_roles() throws NotFoundException, AlreadyCreatedException {
        var userRole = new Role().setId(1L).setName(RoleName.ROLE_USER);
        var adminRole = new Role().setId(2L).setName(RoleName.ROLE_ADMIN);
        when(mockRoleDao.findByRoleName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(mockRoleDao.findByRoleName(RoleName.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));

        sut.execute(username, email, userPassword, Set.of("user", "admin"));
        verify(mockUserDao, times(1)).createUser(username, email, userPassword, Set.of(userRole, adminRole));
    }

    @Test
    void when_user_created_should_return_id_of_new_user() throws NotFoundException, AlreadyCreatedException {
        var adminRole = new Role().setId(2L).setName(RoleName.ROLE_ADMIN);
        when(mockRoleDao.findByRoleName(RoleName.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
        when(mockUserDao.createUser(username, email, userPassword, Set.of(adminRole))).thenReturn(3L);

        var result = sut.execute(username, email, userPassword, Set.of("admin"));
        assertThat(result).isEqualTo(3L);
    }

    @Test
    void when_set_role_null_and_user_role_not_found_should_throw_not_found_exception() {
        when(mockRoleDao.findByRoleName(RoleName.ROLE_USER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.execute(username, email, userPassword, null))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage(SignUp.class + " : role name 'ROLE_USER' not found");
    }
}