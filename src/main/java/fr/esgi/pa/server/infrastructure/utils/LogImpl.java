package fr.esgi.pa.server.infrastructure.utils;

import fr.esgi.pa.server.core.utils.Log;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogImpl<T> implements Log<T> {

    @Override
    public void info(Class<T> aClass, String message) {
        LoggerFactory.getLogger(aClass).info(message);
    }
}
