package eello.elpring.web.mapping;

import eello.elpring.di.annotation.Component;

import java.lang.reflect.*;

@Component
public class ArrayTypeConverter extends CollectionTypeConverter {

    public ArrayTypeConverter(ScalarTypeConverterManager scalarConverterManager) {
        super(scalarConverterManager);
    }

    @Override
    public boolean supports(Class<?> targetType) {
        return targetType.isArray();
    }

    @Override
    protected Object convertInternal(ScalarTypeConverter converter, Class<?> targetType, Class<?> componentType,
                                     String[] rawValues) {
        Object array = Array.newInstance(componentType, rawValues.length);
        for (int i = 0; i < rawValues.length; i++) {
            Array.set(array, i, converter.convertSingle(componentType, rawValues[i]));
        }

        return array;
    }
}
