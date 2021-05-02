package fr.esgi.pa.server.infrastructure.bootstrap;

import fr.esgi.pa.server.core.dao.RoleDao;
import fr.esgi.pa.server.core.exception.AlreadyCreatedException;
import fr.esgi.pa.server.core.model.RoleName;
import fr.esgi.pa.server.core.utils.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class RoleBootstrap {
    private final RoleDao roleDao;
    private final Log<RoleBootstrap> logger;

    @EventListener
    public void on(ApplicationReadyEvent event) {
        var roleNameList = Arrays.asList(RoleName.ROLE_USER, RoleName.ROLE_ADMIN);
        roleNameList.forEach(roleName -> {
            try {
                roleDao.createRole(roleName);
            } catch (AlreadyCreatedException ignored) {
                var message = String.format("Role with name '%s' not save because already created", roleName);
                logger.info(RoleBootstrap.class, message);
            }
        });
    }
}
