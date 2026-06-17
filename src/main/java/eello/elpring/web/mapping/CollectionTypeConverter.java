package eello.elpring.web.mapping;

import eello.elpring.web.exception.MethodArgumentTypeMismatchException;

public abstract class CollectionTypeConverter implements TypeConverter {

    protected final ScalarTypeConverterManager scalarConverterManager;

    public CollectionTypeConverter(ScalarTypeConverterManager scalarConverterManager) {
        this.scalarConverterManager = scalarConverterManager;
    }

    @Override
    public Object convert(Class<?> targetType, String[] rawValues) {
        return convert(targetType, null, rawValues);
    }

    @Override
    public Object convert(Class<?> targetType, Class<?> componentType, String[] rawValues) {
        if (componentType == null) {
            throw new IllegalArgumentException("Component type must not be null");
        }

        ScalarTypeConverter converter = scalarConverterManager.getConverter(componentType);
        if (converter == null) {
            throw new MethodArgumentTypeMismatchException("Cannot convert " + componentType.getName() + " to type " + targetType.getName());
        }

        return convertInternal(converter, targetType, componentType, rawValues);
    }

    protected abstract Object convertInternal(ScalarTypeConverter converter,
                                              Class<?> targetType,
                                              Class<?> componentType,
                                              String[] rawValues);
}
