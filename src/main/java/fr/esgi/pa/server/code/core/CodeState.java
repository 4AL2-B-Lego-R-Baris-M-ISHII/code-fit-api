package fr.esgi.pa.server.code.core;

public enum CodeState {
    SUCCESS("Success"),
    COMPILATION_ERROR("Compilation error"),
    RUNTIME_ERROR("Runtime error"),
    OUT_OF_MEMORY("Out of memory");

    public final String type;
    CodeState(String type) {
        this.type = type;
    }
}
