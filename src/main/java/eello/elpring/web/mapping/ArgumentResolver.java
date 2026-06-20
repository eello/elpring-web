package eello.elpring.web.mapping;

import eello.elpring.web.core.MethodParameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ArgumentResolver {

    boolean supportsParameter(MethodParameter parameter);

    Object resolveArgument(MethodParameter methodParameter, HttpServletRequest request,
                           HttpServletResponse response);
}
