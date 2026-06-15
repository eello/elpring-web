package eello.elpring.web.mapping;

import eello.elpring.di.annotation.Component;
import eello.elpring.web.exception.MethodArgumentTypeMismatchException;

import java.lang.reflect.*;
import java.util.function.Function;

@Component
public class ArrayTypeConverter implements TypeConverter {

    @Override
    public boolean supports(Class<?> targetType) {
        return targetType.isArray();
    }

    @Override
    public Object convert(Class<?> targetType, String[] rawValues) {
        if (!supports(targetType)) {
            throw new IllegalStateException("Cannot convert array of type " + targetType.getName() + " to an array");
        }

        Class<?> componentType = targetType.getComponentType();
        Object array = Array.newInstance(componentType, rawValues.length);

        if (PrimitiveTypeConverter.converters.containsKey(componentType)) {
            Function<String, Object> convertFunc = PrimitiveTypeConverter.converters.get(componentType);
            for (int i = 0; i < rawValues.length; i++) {
                Array.set(array, i, convertFunc.apply(rawValues[i]));
            }
        } else { // 프리미티브 타입이 아닌 경우
            // String 타입의 단일 파라미터를 갖는 생성자 혹은 정적 팩토리 메서드가 있는지 확인
            // 있으면 변환 후 배열에 set, 없으면 예외
            Executable instanceFactory = findSingleStringConstructorOrStaticFactoryMethod(componentType);
            if (instanceFactory == null) {
                throw new MethodArgumentTypeMismatchException("Cannot convert array of type " + targetType.getName() + " to an array");
            }

            try {
                for (int i = 0; i < rawValues.length; i++) {
                    Array.set(array, i, instantiate(instanceFactory, rawValues[i]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return array;
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
