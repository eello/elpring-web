package eello.elpring.web.method.annotation;

import eello.elpring.web.bind.annotation.RequestBody;
import eello.elpring.web.http.MediaType;
import eello.elpring.web.http.converter.HttpMessageConverter;
import eello.elpring.web.http.converter.GenericHttpMessageConverter;
import eello.elpring.web.method.MethodParameter;
import eello.elpring.web.method.support.HandlerMethodArgumentResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class RequestBodyMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final List<HttpMessageConverter<?>> messageConverters;

    public RequestBodyMethodArgumentResolver(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = messageConverters;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.isAnnotationPresent(RequestBody.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, HttpServletRequest request, HttpServletResponse response) {
        String contentType = request.getHeader("Content-Type");
        MediaType mediaType = MediaType.from(contentType);

        Class<?> paramType = methodParameter.getParameterType();
        HttpMessageConverter messageConverter = messageConverters.stream()
                .filter(mc -> mc.canRead(paramType, mediaType))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find HttpMessageConverter for " + mediaType));

        String rawBody = readStringBody(request);

        Object result;
        if (messageConverter instanceof GenericHttpMessageConverter<?> genericHttpMessageConverter) {
            Type genericParameterType = methodParameter.getGenericParameterType();
            result = genericHttpMessageConverter.read(genericParameterType, rawBody);
        } else result = messageConverter.read(paramType, rawBody);

        return result;
    }

    private String readStringBody(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = request.getReader()) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading request body", e);
        }

        return sb.toString();
    }
}
