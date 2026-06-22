package eello.elpring.web.bind.convert;

import eello.elpring.web.exception.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PrimitiveTypeConverter extends ScalarTypeConverter {

    public static final Map<Class<?>, Function<String, Object>> converters = new HashMap<>();

    static {
        // 정수 계열
        converters.put(int.class, Integer::parseInt);
        converters.put(Integer.class, Integer::parseInt);
        converters.put(long.class, Long::parseLong);
        converters.put(Long.class, Long::parseLong);
        converters.put(short.class, Short::parseShort);
        converters.put(Short.class, Short::parseShort);
        converters.put(byte.class, Byte::parseByte);
        converters.put(Byte.class, Byte::parseByte);

        // 실수 계열
        converters.put(double.class, Double::parseDouble);
        converters.put(Double.class, Double::parseDouble);
        converters.put(float.class, Float::parseFloat);
        converters.put(Float.class, Float::parseFloat);

        // 논리 & 문자 계열
        converters.put(boolean.class, Boolean::parseBoolean);
        converters.put(Boolean.class, Boolean::parseBoolean);
    }

    @Override
    public boolean supports(Class<?> targetType) {
        return converters.containsKey(targetType);
    }

    @Override
    protected Object convertSingle(Class<?> targetType, String rawValue) {
        Function<String, Object> converter = converters.get(targetType);
        if (converter == null) {
            throw new MethodArgumentTypeMismatchException("Cannot convert " + rawValue + " to type " + targetType.getName());
        }

        return converter.apply(rawValue);
    }
}
