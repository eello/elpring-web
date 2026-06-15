package eello.elpring.web.mapping;

import eello.elpring.di.annotation.Component;
import eello.elpring.web.annotation.RequestParam;
import eello.elpring.web.core.MethodParameter;
import jakarta.servlet.http.HttpServletRequest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RequestParamMethodArgumentResolver implements ArgumentResolver {

    private final List<TypeConverter> converters;
    private final Map<Class<?>, TypeConverter> convertCache;

    public RequestParamMethodArgumentResolver(List<TypeConverter> converters) {
        this.converters = converters;
        this.convertCache = new ConcurrentHashMap<>();
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (!parameter.isAnnotationPresent(RequestParam.class)) {
            return false;
        }

        Class<?> paramType = parameter.getParameterType();
        if (convertCache.containsKey(paramType)) {
            return true;
        }

        Optional<TypeConverter> matchedConverter =
                converters.stream().filter(converter -> converter.supports(paramType))
                .findFirst();

        if (matchedConverter.isPresent()) {
            convertCache.put(paramType, matchedConverter.get());
            return true;
        }

        return false;
    }

    @Override
    public Object resolveName(String name, MethodParameter methodParameter, HttpServletRequest request) {
        String[] rawValues = request.getParameterValues(name);
        Class<?> paramType = methodParameter.getParameterType();

        TypeConverter typeConverter = convertCache.get(paramType);
        return typeConverter.convert(paramType, rawValues);
    }
}
