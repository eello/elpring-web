package eello.elpring.web.mapping;

import eello.elpring.web.core.MethodParameter;
import jakarta.servlet.http.HttpServletRequest;

public interface ArgumentResolver {

    boolean supportsParameter(MethodParameter parameter);
    Object resolveName(String name, MethodParameter methodParameter, HttpServletRequest request);
}
