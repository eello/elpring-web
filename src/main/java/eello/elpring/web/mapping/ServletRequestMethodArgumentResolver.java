package eello.elpring.web.mapping;

import eello.elpring.web.core.MethodParameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ServletRequestMethodArgumentResolver implements ArgumentResolver {

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
