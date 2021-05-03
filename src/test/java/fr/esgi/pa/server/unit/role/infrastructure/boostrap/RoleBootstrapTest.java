package fr.esgi.pa.server.unit.role.infrastructure.boostrap;

import fr.esgi.pa.server.role.core.RoleDao;
import fr.esgi.pa.server.common.exception.AlreadyCreatedException;
import fr.esgi.pa.server.role.core.RoleName;
import fr.esgi.pa.server.log.core.Log;
import fr.esgi.pa.server.role.infrastructure.bootstrap.RoleBootstrap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleBootstrapTest {
    @Mock
    private ApplicationReadyEvent mockApplicationReadyEvent;

    @Mock
    private RoleDao mockRoleDao;

    @Mock
    private Log<RoleBootstrap> mockLogger;

    private RoleBootstrap sut;

    @BeforeEach
    void setup() {
        sut = new RoleBootstrap(mockRoleDao, mockLogger);
    }

    @Test
    void on_whenNoRoleIsSaved_shouldSaveUserAndAdminRoles() throws AlreadyCreatedException {
        sut.on(mockApplicationReadyEvent);

        verify(mockRoleDao, times(1)).createRole(RoleName.ROLE_USER);
        verify(mockRoleDao, times(1)).createRole(RoleName.ROLE_ADMIN);
    }

    @Test
    void on_when_role_already_create_should_log_info() throws AlreadyCreatedException {
        when(mockRoleDao.createRole(RoleName.ROLE_USER)).thenThrow(new AlreadyCreatedException("exception"));
        when(mockRoleDao.createRole(RoleName.ROLE_ADMIN)).thenReturn(anyLong());

        sut.on(mockApplicationReadyEvent);

        verify(mockLogger, times(1)).info(
                RoleBootstrap.class,
                "Role with name '" + RoleName.ROLE_USER + "' not save because already created"
        );
    }
}