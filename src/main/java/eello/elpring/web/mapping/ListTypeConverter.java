package eello.elpring.web.mapping;

import eello.elpring.di.annotation.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ListTypeConverter extends CollectionTypeConverter {

    public ListTypeConverter(ScalarTypeConverterManager scalarConverterManager) {
        super(scalarConverterManager);
    }

    @Override
    public boolean supports(Class<?> targetType) {
        return List.class.isAssignableFrom(targetType);
    }

    @Override
    protected Object convertInternal(ScalarTypeConverter converter, Class<?> targetType, Class<?> componentType,
                                     String[] rawValues) {
        ArrayList<Object> objects = new ArrayList<>(rawValues.length);

        for (String rawValue : rawValues) {
            objects.add(converter.convertSingle(componentType, rawValue));
        }

        return objects;
    }
}
