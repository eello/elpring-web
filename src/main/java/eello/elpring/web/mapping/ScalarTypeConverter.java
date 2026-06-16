package eello.elpring.web.mapping;

import eello.elpring.web.exception.MethodArgumentTypeMismatchException;

public abstract class ScalarTypeConverter implements TypeConverter {

    @Override
    public Object convert(Class<?> targetType, String[] rawValues) {
        if (!supports(targetType)) {
            throw new MethodArgumentTypeMismatchException("Cannot convert to Scalar type: unsupported target type [" + targetType.getName() + "]");
        }

        return convertSingle(targetType, rawValues[0]);
    }

    protected abstract Object convertSingle(Class<?> targetType, String rawValue);
}
