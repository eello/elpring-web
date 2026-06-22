package eello.elpring.web.method.annotation;

import eello.elpring.web.bind.annotation.ModelAttribute;
import eello.elpring.web.bind.support.TypeConversionService;
import eello.elpring.web.exception.MethodArgumentTypeMismatchException;
import eello.elpring.web.inbox.ClassUtils;
import eello.elpring.web.method.MethodParameter;
import eello.elpring.web.method.support.HandlerMethodArgumentResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ModelAttributeMethodArgumentResolver implements HandlerMethodArgumentResolver {

    /*
        가장 우선순위가 낮은 HandlerMethodArgumentResolver로 쿼리스트링의 값을 가지고 파라미터의 타입의 객체를 생성함.
        파라미터 타입의 기본 생성자(파라미터가 없는 생성자)를 사용해 인스턴스를 만들고 setter 메소드를 통해 값을 주입
        반드시 기본 생성자가 있어야하며 값을 넣기 위해서는 setter가 정의되어야 함.
     */

    private final TypeConversionService typeConversionService;
    private final Map<Class<?>, Constructor<?>> constructorCache = new ConcurrentHashMap<>();

    public ModelAttributeMethodArgumentResolver(TypeConversionService typeConversionService) {
        this.typeConversionService = typeConversionService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.isAnnotationPresent(ModelAttribute.class)) {
            return true;
        }

        // 최소한 인터페이스나 프리미티브 타입(int, long)은 거르고, 팩토리에서 new 할 수 있는 진짜 일반 클래스 객체일 때만 수락
        // 프리미티브 타입이나 String은 이미 이 전의 RequestParamMethodArgumentResolver에서 처리하기 때문
        return !ClassUtils.isSimpleType(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, HttpServletRequest request,
                                  HttpServletResponse response) {
        Class<?> type = methodParameter.getParameterType();
        Constructor<?> defaultConstructor = getAnyDefaultConstructor(type);
        if (defaultConstructor == null) {
            throw new MethodArgumentTypeMismatchException("");
        }

        try {
            Object object = defaultConstructor.newInstance();
            Map<Method, String> setterMap = Arrays.stream(type.getMethods())
                    .filter(method -> method.getName().startsWith("set") && method.getParameterCount() == 1)
                    .collect(Collectors.toMap(
                            method -> method,
                            method -> Introspector.decapitalize(method.getName().substring(3)).trim()
                    ));

            for (Map.Entry<Method, String> setterInfo : setterMap.entrySet()) {
                Method setter = setterInfo.getKey();
                String fieldName = setterInfo.getValue();

                Class<?> fieldType = setter.getParameterTypes()[0];
                if (!typeConversionService.supports(fieldType)) {
                    // fieldType을 처리할 수 있는 타입 컨버터가 존재하지 않음.
                    throw new IllegalStateException(fieldType + " 을 처리할 수 있는 TypeConverter가 존재하지 않음.");
                }

                String[] values = request.getParameterValues(fieldName);
                if (values == null || values.length == 0) {
                    continue;
                }

                Object arg = typeConversionService.convert(fieldType, values);
                setter.invoke(object, arg);
            }

            return object;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Constructor<?> getAnyDefaultConstructor(Class<?> clazz) {
        if (constructorCache.containsKey(clazz)) {
            return constructorCache.get(clazz);
        }

        // [방어선] 인터페이스, 원시 타입 등 물리적으로 생성자가 없는 구조는 바로 컷!
        if (clazz.isInterface() || clazz.isPrimitive() || clazz.isAnnotation() || clazz.isEnum()) {
            return null;
        }

        // 추상 클래스도 직접 new할 수 없으므로 제외
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return null;
        }

        try {
            Constructor<?> defaultConstructor = clazz.getDeclaredConstructor();
            defaultConstructor.setAccessible(true); // 접근제한자가 public이 아니더라도 접근할 수 있도록 설정
            constructorCache.put(clazz, defaultConstructor);

            return defaultConstructor;
        } catch (NoSuchMethodException e) {
            throw new MethodArgumentTypeMismatchException(e.getMessage());
        } catch (SecurityException e) {
            return null;
        }
    }
}
