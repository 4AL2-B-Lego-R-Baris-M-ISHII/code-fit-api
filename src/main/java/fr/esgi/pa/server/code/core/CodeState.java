package fr.esgi.pa.server.code.core;

public enum CodeState {
    SUCCESS("Success"),
    COMPILATION_ERROR("Compilation error"),
    RUNTIME_ERROR("Runtime error"),
    OUT_OF_MEMORY("Out of memory"),
    TIME_LIMIT_EXCEED("Time limit exceed"),
    OTHER_ERROR("Other error");

    public final String type;
    CodeState(String type) {
        this.type = type;
    }
}
