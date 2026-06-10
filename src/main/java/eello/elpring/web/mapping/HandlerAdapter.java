package eello.elpring.web.mapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface HandlerAdapter {

    String handle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception;
}
