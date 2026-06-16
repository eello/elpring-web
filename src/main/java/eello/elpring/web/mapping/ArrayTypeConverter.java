package eello.elpring.web.mapping;

import eello.elpring.di.annotation.Component;

import java.lang.reflect.*;

@Component
public class ArrayTypeConverter extends CollectionTypeConverter {

    private final ScalarTypeConverterManager scalarConverterManager;

    public ArrayTypeConverter(ScalarTypeConverterManager scalarConverterManager) {
        this.scalarConverterManager = scalarConverterManager;
    }

    @Override
    public boolean supports(Class<?> targetType) {
        return targetType.isArray() && scalarConverterManager.supports(targetType.getComponentType());
    }

    @Override
    protected Object convertInternal(Class<?> targetType, String[] rawValues) {
        Class<?> componentType = targetType.getComponentType();
        ScalarTypeConverter converter = scalarConverterManager.getConverter(componentType);

        Object array = Array.newInstance(componentType, rawValues.length);
        for (int i = 0; i < rawValues.length; i++) {
            Array.set(array, i, converter.convertSingle(componentType, rawValues[i]));
        }

        return array;
    }
}
