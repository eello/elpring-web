package eello.elpring.web.mapping;

import eello.elpring.di.annotation.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ScalarTypeConverterManager {

    private final List<ScalarTypeConverter> converters;
    private final Map<Class<?>, ScalarTypeConverter> cache = new ConcurrentHashMap<>();

    public ScalarTypeConverterManager(List<ScalarTypeConverter> converters) {
        this.converters = converters;
    }

    public boolean supports(Class<?> targetType) {
        if (cache.containsKey(targetType)) {
            return true;
        }

        for (ScalarTypeConverter converter : converters) {
            if (converter.supports(targetType)) {
                cache.put(targetType, converter);
                return true;
            }
        }
        return false;
    }

    public ScalarTypeConverter getConverter(Class<?> targetType) {
        if (cache.containsKey(targetType)) {
            return cache.get(targetType);
        }

        for (ScalarTypeConverter converter : converters) {
            if (converter.supports(targetType)) {
                return converter;
            }
        }
        return null;
    }
}
