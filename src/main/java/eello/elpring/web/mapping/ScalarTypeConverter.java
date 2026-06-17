package eello.elpring.web.mapping;

public abstract class ScalarTypeConverter implements TypeConverter {

    @Override
    public Object convert(Class<?> targetType, String[] rawValues) {
        return convert(targetType, null, rawValues);
    }

    @Override
    public Object convert(Class<?> targetType, Class<?> componentType, String[] rawValues) {
        return convertSingle(targetType, rawValues[0]);
    }

    protected abstract Object convertSingle(Class<?> targetType, String rawValue);
}
