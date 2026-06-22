package eello.elpring.web.bind.support;
import eello.elpring.web.bind.convert.TypeConverter;
import eello.elpring.web.bind.convert.TypeConverterManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestParamConversionService {

    private final List<TypeConverterManager<? extends TypeConverter>> typeConverterManagers;
    private final Map<Class<?>, TypeConverter> convertCache = new ConcurrentHashMap<>();

    public RequestParamConversionService(List<TypeConverterManager<? extends TypeConverter>> typeConverterManagers) {
        this.typeConverterManagers = typeConverterManagers;
    }

    public boolean supports(Class<?> paramType) {
        if (convertCache.containsKey(paramType)) {
            return true;
        }

        return getConverter(paramType) != null;
    }

    public Object convert(Class<?> paramType, String[] rawValues) {
        return convert(paramType, null, rawValues);
    }

    public Object convert(Class<?> paramType, Class<?> componentType, String[] rawValues) {
        TypeConverter converter = getConverter(paramType);
        return converter.convert(paramType, componentType, rawValues);
    }

    public TypeConverter getConverter(Class<?> paramType) {
        return convertCache.computeIfAbsent(paramType, this::findConverter);
    }

    private TypeConverter findConverter(Class<?> paramType) {
        TypeConverterManager<? extends TypeConverter> typeConverterManager = typeConverterManagers.stream()
                .filter(manager -> manager.supports(paramType))
                .findFirst()
                .orElse(null);

        return typeConverterManager != null ? typeConverterManager.getTypeConverter(paramType) : null;
    }
}
