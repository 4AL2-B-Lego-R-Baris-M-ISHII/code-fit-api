package fr.esgi.pa.server.core.utils;

public interface Log<T> {
    void info(Class<T> aClass, String message);
}
