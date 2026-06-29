package eello.elpring.web.http.converter;

import eello.elpring.web.http.MediaType;
import java.lang.reflect.Type;

public interface GenericHttpMessageConverter<T> extends HttpMessageConverter<T> {

    boolean canRead(Type targetType, MediaType mediaType);
    T read(Type targetType, String rawBody);
}
