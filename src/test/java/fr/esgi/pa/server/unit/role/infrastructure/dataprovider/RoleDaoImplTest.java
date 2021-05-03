package fr.esgi.pa.server.unit.role.infrastructure.dataprovider;

import fr.esgi.pa.server.role.core.RoleDao;
import fr.esgi.pa.server.common.exception.AlreadyCreatedException;
import fr.esgi.pa.server.role.core.RoleName;
import fr.esgi.pa.server.role.infrastructure.dataprovider.RoleDaoImpl;
import fr.esgi.pa.server.role.infrastructure.dataprovider.RoleEntity;
import fr.esgi.pa.server.role.infrastructure.dataprovider.RoleMapper;
import fr.esgi.pa.server.role.infrastructure.dataprovider.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleDaoImplTest {
    @Mock
    private RoleRepository mockRoleRepository;

    private final RoleMapper roleMapper = new RoleMapper();

    private RoleDaoImpl sut;

    @BeforeEach
    void setup() {
        sut = new RoleDaoImpl(mockRoleRepository, roleMapper);
    }

    @Nested
    class FindByRoleName {
        @Test
        void should_call_findByRoleName_of_repository() {
            sut.findByRoleName(RoleName.ROLE_USER);
            verify(mockRoleRepository, times(1)).findByName(RoleName.ROLE_USER);
        }

        @Test
        void should_return_role_by_role_name() {
            var roleEntity = new RoleEntity().setId(1L).setName(RoleName.ROLE_ADMIN);
            when(mockRoleRepository.findByName(RoleName.ROLE_ADMIN)).thenReturn(Optional.of(roleEntity));

            var expectedRole = roleMapper.entityToDomain(roleEntity);

            var result = sut.findByRoleName(RoleName.ROLE_ADMIN);

            assertThat(result.isPresent()).isTrue();
            assertThat(result.get()).isEqualTo(expectedRole);
        }

        @Test
        void when_repository_not_found_role_should_return_null() {
            when(mockRoleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.empty());

            var result = sut.findByRoleName(RoleName.ROLE_USER);

            assertThat(result.isPresent()).isFalse();
        }
    }

    @Nested
    class CreateRole {
        @Test
        void when_role_already_created_should_throw_AlreadyCreatedException() {
            var roleEntity = new RoleEntity().setId(1L).setName(RoleName.ROLE_USER);
            when(mockRoleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(roleEntity));

            assertThatThrownBy(() -> sut.createRole(RoleName.ROLE_USER))
                    .isExactlyInstanceOf(AlreadyCreatedException.class)
                    .hasMessage(RoleDao.class + " : role with name '" + RoleName.ROLE_USER + "' already exists");
        }

        @Test
        void when_role_saved_should_return_new_role_id() throws AlreadyCreatedException {
            var adminRole = new RoleEntity().setName(RoleName.ROLE_ADMIN);
            var savedRole = new RoleEntity().setId(3L).setName(RoleName.ROLE_ADMIN);
            when(mockRoleRepository.findByName(RoleName.ROLE_ADMIN)).thenReturn(Optional.empty());
            when(mockRoleRepository.save(adminRole)).thenReturn(savedRole);

            var result = sut.createRole(RoleName.ROLE_ADMIN);

            assertThat(result).isEqualTo(3L);
        }
    }

    @Test
    void findAll_should_find_all_roles_by_repository() {
        var userRole = new RoleEntity().setId(1L).setName(RoleName.ROLE_USER);
        var adminRole = new RoleEntity().setId(2L).setName(RoleName.ROLE_ADMIN);
        var rolesEntityList = List.of(userRole, adminRole);
        when(mockRoleRepository.findAll()).thenReturn(rolesEntityList);

        var expectedRoleList = rolesEntityList.stream()
                .map(roleMapper::entityToDomain)
                .collect(Collectors.toList());

        var result = sut.findAll();

        assertThat(result).isEqualTo(expectedRoleList);
    }
}