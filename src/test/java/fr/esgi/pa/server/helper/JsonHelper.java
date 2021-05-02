package fr.esgi.pa.server.helper;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {
    public static String objectToJson(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
