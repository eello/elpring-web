package eello.elpring.web.mapping;

import eello.elpring.di.annotation.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class SetTypeConverter extends CollectionTypeConverter {

    public SetTypeConverter(ScalarTypeConverterManager scalarConverterManager) {
        super(scalarConverterManager);
    }

    @Override
    public boolean supports(Class<?> targetType) {
        return Set.class.isAssignableFrom(targetType);
    }

    @Override
    protected Object convertInternal(ScalarTypeConverter converter, Class<?> targetType, Class<?> componentType,
                                     String[] rawValues) {
        Set<Object> objects = new HashSet<>();

        for (String rawValue : rawValues) {
            objects.add(converter.convertSingle(componentType, rawValue));
        }

        return objects;
    }
}
