package eello.elpring.web.core;

import eello.elpring.web.mapping.HandlerMethod;

public class HandlerExecutionChain {

    private final HandlerMethod handler;

    public HandlerExecutionChain(HandlerMethod handler) {
        this.handler = handler;
    }

    public HandlerMethod getHandler() {
        return handler;
    }
}
