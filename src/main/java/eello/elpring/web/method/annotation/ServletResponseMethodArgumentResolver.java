package eello.elpring.web.method.annotation;
import eello.elpring.web.method.support.HandlerMethodArgumentResolver;

import eello.elpring.web.method.MethodParameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ServletResponseMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return HttpServletResponse.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, HttpServletRequest request,
                                  HttpServletResponse response) {
        return response;
    }
}
