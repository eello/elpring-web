package eello.elpring.web.mapping;

import eello.elpring.web.core.HandlerExecutionChain;
import jakarta.servlet.http.HttpServletRequest;

public interface HandlerMapping {

    HandlerExecutionChain getHandler(HttpServletRequest request);
}
