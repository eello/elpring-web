package eello.elpring.web.mapping;

import eello.elpring.web.exception.MethodArgumentTypeMismatchException;

public abstract class CollectionTypeConverter implements TypeConverter {

    @Override
    public Object convert(Class<?> targetType, String[] rawValues) {
        if (!supports(targetType)) {
            throw new MethodArgumentTypeMismatchException("Cannot convert to Collection/Array type: unsupported " +
                    "target type [" + targetType.getName() + "]");
        }

        return convertInternal(targetType, rawValues);
    }

    protected abstract Object convertInternal(Class<?> targetType, String[] rawValues);
}
