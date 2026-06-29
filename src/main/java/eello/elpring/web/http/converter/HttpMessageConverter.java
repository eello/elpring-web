package eello.elpring.web.http.converter;

import eello.elpring.web.http.MediaType;

public interface HttpMessageConverter<T> {

    boolean canRead(Class<?> targetType, MediaType mediaType);
    T read(Class<? extends T> targetType, String rawBody);
}
