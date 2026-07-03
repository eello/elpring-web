package eello.elpring.web.method.annotation;

import eello.elpring.web.bind.support.TypeConversionService;
import eello.elpring.web.exception.MissingServletRequestParameterException;
import eello.elpring.web.util.ClassUtils;
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
    private final boolean useDefaultResolution;

    public RequestParamMethodArgumentResolver(TypeConversionService typeConversionService) {
        this(typeConversionService, false);
    }

    public RequestParamMethodArgumentResolver(TypeConversionService typeConversionService,
                                              boolean useDefaultResolution) {
        this.typeConversionService = typeConversionService;
        this.useDefaultResolution = useDefaultResolution;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.isAnnotationPresent(RequestParam.class)) {
            return true;
        }

        return useDefaultResolution && ClassUtils.isSimpleType(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, HttpServletRequest request,
                                  HttpServletResponse response) {
        String paramName = resolveAnnotatedParameterName(methodParameter, RequestParam.class, RequestParam::value);
        String[] rawValues = request.getParameterValues(paramName);
        Class<?> paramType = methodParameter.getParameterType();
        RequestParam requestParam = methodParameter.getAnnotation(RequestParam.class);

        /*
            ########################################################################
            @RequestParam.required = false 일 때
            - rawValues == null 일때 (paramName에 해당하는 쿼리 파라미터가 입력으로 주어지지 않았을 때)
            - return required ? throw IllegalStateException : null
            ########################################################################
        */
        if (rawValues == null || rawValues.length == 0) {
            boolean required = (requestParam != null) ? requestParam.required() : false;
            if (required) {
                throw new MissingServletRequestParameterException(paramName);
            }

            if (paramType.isPrimitive()) {
                throw new IllegalStateException("Optional " + paramType.getSimpleName() + " parameter '" + paramName + "' is present but cannot be translated into a null value due to being declared as a primitive type.");
            }

            return null;
        }

        // 만약 List, Set과 같은 Collection이 아닌 다른 제네릭 타입을 지원할 것이라면 수정 필요
        Class<?> componentType = null;
        if (paramType.isArray()) {
            componentType = paramType.getComponentType();
        } else if (Collection.class.isAssignableFrom(paramType)) {
            componentType = GenericTypeResolver.getGenericComponentType(methodParameter.getParameter());
        }

        return typeConversionService.convert(paramType, componentType, rawValues);
    }
}
