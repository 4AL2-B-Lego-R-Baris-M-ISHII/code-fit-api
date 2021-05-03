package fr.esgi.pa.server.log.core;

public interface Log<T> {
    void info(Class<T> aClass, String message);
}
