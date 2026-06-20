package eello.elpring.web.mapping;

import eello.elpring.web.core.MethodParameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;

public class RequestMappingHandlerAdapter implements HandlerAdapter {

    private final ObjectMapper objectMapper;
    private final HandlerMethodArgumentResolverComposite argumentResolver;


    public RequestMappingHandlerAdapter(ObjectMapper objectMapper,
                                        HandlerMethodArgumentResolverComposite argumentResolver) {
        this.objectMapper = objectMapper;
        this.argumentResolver = argumentResolver;
    }

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception {
        Object controller = handler.getBean();
        Method handlerMethod = handler.getMethod();

        MethodParameter[] parameters = handler.getParameters();
        Object[] args = new Object[parameters.length];

        int argIndex = 0;
        for (MethodParameter param : parameters) {
            args[argIndex++] = argumentResolver.resolveArgument(param, request, response);
        }

        // 반환 타입을 JSON 객체로 반환
        return objectMapper.writeValueAsString(handlerMethod.invoke(controller, args));
    }
}
