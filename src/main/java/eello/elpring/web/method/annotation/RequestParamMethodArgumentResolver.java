package eello.elpring.web.method.annotation;
import eello.elpring.web.bind.support.TypeConversionService;
import eello.elpring.web.inbox.ClassUtils;
import eello.elpring.web.method.support.HandlerMethodArgumentResolver;

import eello.elpring.di.util.GenericTypeResolver;
import eello.elpring.web.bind.annotation.RequestParam;
import eello.elpring.web.method.MethodParameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collection;

public class RequestParamMethodArgumentResolver implements HandlerMethodArgumentResolver {

    /*
        @RequestParam이 적용됐거나, 적용되지 않은 기본형 타입(Primitive, Enum, String 등)에 대해 처리를 담
     */

    private final TypeConversionService typeConversionService;

    public RequestParamMethodArgumentResolver(TypeConversionService typeConversionService) {
        this.typeConversionService = typeConversionService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.isAnnotationPresent(RequestParam.class)) {
            return true;
        }

        return ClassUtils.isSimpleType(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, HttpServletRequest request,
                                  HttpServletResponse response) {
        String paramName = resolveName(methodParameter);
        String[] rawValues = request.getParameterValues(paramName);
        Class<?> paramType = methodParameter.getParameterType();

        // 만약 List, Set과 같은 Collection이 아닌 다른 제네릭 타입을 지원할 것이라면 수정 필요
        Class<?> componentType = null;
        if (paramType.isArray()) {
            componentType = paramType.getComponentType();
        } else if (Collection.class.isAssignableFrom(paramType)) {
            componentType = GenericTypeResolver.getGenericComponentType(methodParameter.getParameter());
        }

        return typeConversionService.convert(paramType, componentType, rawValues);
    }

    private String resolveName(MethodParameter methodParameter) {
        String paramName = methodParameter.getParameterName();

        RequestParam requestParam = methodParameter.getAnnotation(RequestParam.class);
        if (requestParam != null && !requestParam.value().isBlank()) {
            paramName = requestParam.value();
        }

        if (paramName == null || paramName.startsWith("arg")) {
            throw new IllegalArgumentException(
                    "Name for argument of type [" + methodParameter.getParameterType().getName() +
                            "] not specified, and parameter name information not available via reflection. " +
                            "이름을 알 수 없으니 컴파일 시 -parameters 옵션을 켜거나 @RequestParam에 이름을 명시해주세요."
            );
        }

        return paramName;
    }
}
