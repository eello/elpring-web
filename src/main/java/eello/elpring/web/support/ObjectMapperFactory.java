package eello.elpring.web.support;

import tools.jackson.databind.ObjectMapper;

public class ObjectMapperFactory {

    private ObjectMapper objectMapper;

    public ObjectMapperFactory() {
        objectMapper = new ObjectMapper();
    }

    public ObjectMapper get() {
        return objectMapper;
    }
}
