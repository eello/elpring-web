package eello.elpring.web.servlet;

import eello.elpring.web.servlet.HandlerExecutionChain;
import jakarta.servlet.http.HttpServletRequest;

public interface HandlerMapping {

    HandlerExecutionChain getHandler(HttpServletRequest request);
}
