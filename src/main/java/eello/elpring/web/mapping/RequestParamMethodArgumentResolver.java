package eello.elpring.web.mapping;

import eello.elpring.di.annotation.Component;
import eello.elpring.web.annotation.RequestParam;
import eello.elpring.web.core.MethodParameter;
import jakarta.servlet.http.HttpServletRequest;

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

        return requestParamConversionService.convert(paramType, rawValues);
    }
}
