package fr.esgi.pa.server.code.infrastructure.device;

import fr.esgi.pa.server.code.core.CodeState;

public interface CodeStateHelper {
    CodeState getCodeState(Integer status);
}
