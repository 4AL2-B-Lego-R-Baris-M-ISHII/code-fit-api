package fr.esgi.pa.server.log.infrastructure;

import fr.esgi.pa.server.log.core.Log;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Slf4jLog<T> implements Log<T> {

    @Override
    public void info(Class<T> aClass, String message) {
        LoggerFactory.getLogger(aClass).info(message);
    }
}
