package fr.esgi.pa.server.code.infrastructure.device.helper;

import fr.esgi.pa.server.code.core.compiler.CodeState;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CodeStateHelperImpl implements CodeStateHelper {

    private final Map<Integer, CodeState> statusCodeStateMap;

    public CodeStateHelperImpl() {
        this.statusCodeStateMap = new HashMap<>();
        statusCodeStateMap.put(0, CodeState.SUCCESS);
        statusCodeStateMap.put(1, CodeState.RUNTIME_ERROR);
        statusCodeStateMap.put(2, CodeState.COMPILATION_ERROR);
        statusCodeStateMap.put(124, CodeState.TIME_LIMIT_EXCEED);
        statusCodeStateMap.put(139, CodeState.OUT_OF_MEMORY);
    }

    public CodeState getCodeState(Integer status) {
        return statusCodeStateMap.getOrDefault(status, CodeState.OTHER_ERROR);
    }
}
