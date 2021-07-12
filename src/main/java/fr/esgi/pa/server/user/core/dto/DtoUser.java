package fr.esgi.pa.server.user.core.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DtoUser {
    private Long id;
    private String username;
    private String email;
}
