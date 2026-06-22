package eello.elpring.web.method.annotation;
import eello.elpring.web.method.support.HandlerMethodArgumentResolver;

import eello.elpring.web.method.MethodParameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ServletRequestMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> type = parameter.getParameterType();
        return HttpServletRequest.class.isAssignableFrom(type);
//        || HttpSession.class.isAssignableFrom(type)
//        || java.io.InputStream.class.isAssignableFrom(type);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, HttpServletRequest request,
                                  HttpServletResponse response) {
        return request;
    }
}
