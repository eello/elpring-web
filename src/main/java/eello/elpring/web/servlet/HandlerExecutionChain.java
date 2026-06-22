package eello.elpring.web.servlet;

import eello.elpring.web.method.HandlerMethod;

public class HandlerExecutionChain {

    private final HandlerMethod handler;

    public HandlerExecutionChain(HandlerMethod handler) {
        this.handler = handler;
    }

    public HandlerMethod getHandler() {
        return handler;
    }
}
