package eello.elpring.web.util;

public class ClassUtils {

    private ClassUtils() {
        throw new AssertionError("Utility class는 인스턴스로 생성할 수 없음.");
    }

    public static boolean isSimpleType(Class<?> clazz) {
        if (clazz == null) return false;

        return clazz.isPrimitive() ||
                clazz == String.class ||
                Number.class.isAssignableFrom(clazz) ||
                Boolean.class == clazz ||
                Character.class == clazz ||
                clazz.isEnum();
    }
}
