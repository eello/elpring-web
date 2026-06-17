package eello.elpring.web.mapping;

import eello.elpring.di.annotation.Component;
import eello.elpring.di.util.GenericTypeResolver;
import eello.elpring.web.annotation.RequestParam;
import eello.elpring.web.core.MethodParameter;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;

@Component
public class RequestParamMethodArgumentResolver implements ArgumentResolver {

    /**
     * TODO: CustomObjectTypeConvert, ListTypeConverter, SetTypeConverter 추가
     * TODO: ArrayTypeConverter에서 객체 생성하는 부분을 CustomObjectTypeConverter에 옮기고 그걸 활용하도록 수정
     */

    private final RequestParamConversionService requestParamConversionService;

    public RequestParamMethodArgumentResolver(RequestParamConversionService requestParamConversionService) {
        this.requestParamConversionService = requestParamConversionService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (!parameter.isAnnotationPresent(RequestParam.class)) {
            return false;
        }

        return requestParamConversionService.supports(parameter.getParameterType());
    }

    @Override
    public Object resolveName(String name, MethodParameter methodParameter, HttpServletRequest request) {
        String[] rawValues = request.getParameterValues(name);
        Class<?> paramType = methodParameter.getParameterType();

        // 만약 List, Set과 같은 Collection이 아닌 다른 제네릭 타입을 지원할 것이라면 수정 필요
        Class<?> componentType = null;
        if (paramType.isArray()) {
            componentType = paramType.getComponentType();
        } else if (Collection.class.isAssignableFrom(paramType)) {
            componentType = GenericTypeResolver.getGenericComponentType(methodParameter.getParameter());
        }

        return requestParamConversionService.convert(paramType, componentType, rawValues);
    }
}
