package fr.esgi.pa.server.infrastructure.entrypoint.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MessageResponse {
    private final String message;
}
