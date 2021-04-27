package fr.esgi.pa.server.unit.infrastructure.dao;

import fr.esgi.pa.server.core.model.RoleName;
import fr.esgi.pa.server.infrastructure.dao.RoleDaoImpl;
import fr.esgi.pa.server.infrastructure.dataprovider.entity.RoleEntity;
import fr.esgi.pa.server.infrastructure.dataprovider.mapper.RoleMapper;
import fr.esgi.pa.server.infrastructure.dataprovider.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
}