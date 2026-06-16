package eello.elpring.web.mapping;

import eello.elpring.di.annotation.Component;
import eello.elpring.web.exception.MethodArgumentTypeMismatchException;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomObjectTypeConverter extends ScalarTypeConverter {

    private final Map<Class<?>, Executable> instanceFactory = new HashMap<>();

    @Override
    public boolean supports(Class<?> targetType) {
        if (targetType.isArray()
                || Collection.class.isAssignableFrom(targetType)
                || targetType.isPrimitive()) {
            return false;
        }

        if (targetType.isInterface() || Map.class.isAssignableFrom(targetType)) {
            return false;
        }

        return getInstanceFactory(targetType) != null;
    }

    @Override
    protected Object convertSingle(Class<?> targetType, String rawValue) {
        Executable instanceFactory = getInstanceFactory(targetType);
        if (instanceFactory == null) {
            throw new MethodArgumentTypeMismatchException("Cannot convert " + targetType.getName() + " to a " + this.getClass().getName());
        }

        Object instance = null;
        try {
            instance = instantiate(instanceFactory, rawValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return instance;
    }

    private Executable getInstanceFactory(Class<?> targetType) {
        if (!instanceFactory.containsKey(targetType)) {
            instanceFactory.put(targetType, findSingleStringConstructorOrStaticFactoryMethod(targetType));
        }

        return instanceFactory.get(targetType);
    }

    private Object instantiate(Executable instanceFactory, String arg) throws InvocationTargetException,
            IllegalAccessException, InstantiationException {
        Object instance = null;
        if (instanceFactory instanceof Constructor) {
            instance = ((Constructor<?>) instanceFactory).newInstance(arg);
        } else if (instanceFactory instanceof Method) {
            instance = ((Method) instanceFactory).invoke(null, arg);
        }
        return instance;
    }

    /*
        단일 String 파라미터를 갖는 생성자 혹은 정적 팩토리 메서드 중 하나를 찾아 리턴
     */
    private Executable findSingleStringConstructorOrStaticFactoryMethod(Class<?> type) {
        for (Constructor<?> constructor : type.getConstructors()) {
            if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0] == String.class) {
                return constructor;
            }
        }

        for (Method method : type.getMethods()) {
            if (isSingleStringStaticFactoryMethod(type, method)) {
                return method;
            }
        }

        return null;
    }

    private boolean isSingleStringStaticFactoryMethod(Class<?> type, Method method) {
        return Modifier.isStatic(method.getModifiers())
                && type.isAssignableFrom(method.getReturnType()) // 하위 타입 리턴까지 안전하게 수용하도록 다형성 확장
                && method.getParameterCount() == 1
                && method.getParameterTypes()[0] == String.class;
    }
}
