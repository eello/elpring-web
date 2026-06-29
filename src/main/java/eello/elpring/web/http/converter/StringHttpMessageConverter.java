package eello.elpring.web.http.converter;

import eello.elpring.web.http.MediaType;

public class StringHttpMessageConverter implements HttpMessageConverter<String> {

    @Override
    public boolean canRead(Class<?> targetType, MediaType mediaType) {
        return String.class == targetType && MediaType.TEXT_PLAIN.equals(mediaType);
    }

    @Override
    public String read(Class<? extends String> targetType, String rawBody) {
        return rawBody;
    }
}
