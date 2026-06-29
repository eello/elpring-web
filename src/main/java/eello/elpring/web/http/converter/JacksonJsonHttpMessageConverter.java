package eello.elpring.web.http.converter;

import eello.elpring.web.http.MediaType;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.lang.reflect.Type;

public class JacksonJsonHttpMessageConverter implements GenericHttpMessageConverter<Object> {

    private final ObjectMapper objectMapper;

    public JacksonJsonHttpMessageConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean canRead(Class<?> targetType, MediaType mediaType) {
        return canRead((Type) targetType, mediaType);
    }

    @Override
    public boolean canRead(Type targetType, MediaType mediaType) {
        if (!MediaType.APPLICATION_JSON.equals(mediaType)) {
            return false;
        }

        JavaType javaType = objectMapper.getTypeFactory().constructType(targetType);
        Class<?> rawClass = javaType.getRawClass();

        return supportsType(rawClass);
    }

    private boolean supportsType(Class<?> targetType) {
        if (targetType == String.class) return false;      // StringConverter가 해야 함
        if (targetType == byte[].class) return false;    // 바이너리 다운로드 등 바이트 컨버터가 해야 함
        if (InputStream.class.isAssignableFrom(targetType)) return false; // 스트림 그 자체

        // 팩트: 위의 특수 타입들을 제외한 모든 커스텀 DTO, Map, List 등은
        // Jackson ObjectMapper가 전부 객체로 바인딩
        return true;
    }

    @Override
    public Object read(Class<?> targetType, String rawBody) {
        return read((Type) targetType, rawBody);
    }

    @Override
    public Object read(Type targetType, String rawBody) {
        JavaType javaType = objectMapper.getTypeFactory().constructType(targetType);
        return objectMapper.readValue(rawBody, javaType);
    }
}
