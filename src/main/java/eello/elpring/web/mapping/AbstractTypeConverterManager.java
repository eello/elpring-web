package eello.elpring.web.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractTypeConverterManager<T extends TypeConverter> implements TypeConverterManager<T> {

    private final List<T> converters;
    private final Map<Class<?>, T> cache;

    public AbstractTypeConverterManager() {
        this(new ArrayList<>());
    }

    public AbstractTypeConverterManager(List<T> converters) {
        this.converters = converters;
        this.cache = new HashMap<>();
    }

    @Override
    public TypeConverter getTypeConverter(Class<?> targetType) {
        if (cache.containsKey(targetType)) {
            return cache.get(targetType);
        }

        for (T converter : converters) {
            if (converter.supports(targetType)) {
                return converter;
            }
        }

        return null;
    }

    @Override
    public void addTypeConverter(T converter) {
        this.converters.add(converter);
    }

    @Override
    public boolean supports(Class<?> targetType) {
        if (cache.containsKey(targetType)) {
            return true;
        }

        for (T converter : converters) {
            if (converter.supports(targetType)) {
                cache.put(targetType, converter);
                return true;
            }
        }
        return false;
    }
}
