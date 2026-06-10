package eello.elpring.web.support;

import eello.elpring.di.annotation.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class ObjectMapperFactory {

    private ObjectMapper objectMapper;

    public ObjectMapperFactory() {
        objectMapper = new ObjectMapper();
    }

    public ObjectMapper get() {
        return objectMapper;
    }
}
