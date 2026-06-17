package eello.elpring.web.mapping;

import eello.elpring.di.annotation.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RequestParamConversionService {

    private final ScalarTypeConverterManager scalarConverterManager;
    private final List<CollectionTypeConverter> collectionConverters;
    private final Map<Class<?>, TypeConverter> convertCache = new ConcurrentHashMap<>();

    public RequestParamConversionService(ScalarTypeConverterManager scalarConverterManager,
                                         List<CollectionTypeConverter> collectionConverters) {
        this.scalarConverterManager = scalarConverterManager;
        this.collectionConverters = collectionConverters;
    }

    public boolean supports(Class<?> paramType) {
        if (convertCache.containsKey(paramType)) {
            return true;
        }

        if (scalarConverterManager.supports(paramType)) {
            ScalarTypeConverter converter = scalarConverterManager.getConverter(paramType);
            convertCache.put(paramType, converter);
            return true;
        }

        Optional<CollectionTypeConverter> matchedCollectionConverter =
                collectionConverters.stream().filter(converter -> converter.supports(paramType))
                        .findFirst();

        if (matchedCollectionConverter.isPresent()) {
            convertCache.put(paramType, matchedCollectionConverter.get());
            return true;
        }

        return false;
    }

    public Object convert(Class<?> paramType, String[] rawValues) {
        return convert(paramType, null, rawValues);
    }

    public Object convert(Class<?> paramType, Class<?> componentType, String[] rawValues) {
        TypeConverter converter = convertCache.get(paramType);
        return converter.convert(paramType, componentType, rawValues);
    }

    public TypeConverter getConverter(Class<?> paramType) {
        if (!supports(paramType)) {
            return null;
        }

        return convertCache.get(paramType);
    }
}
