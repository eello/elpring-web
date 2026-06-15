package eello.elpring.web.mapping;

import eello.elpring.di.annotation.Component;
import eello.elpring.web.support.ObjectMapperFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;

@Component
public class RequestMappingHandlerAdapter implements HandlerAdapter {

    private final ObjectMapper objectMapper;

    public RequestMappingHandlerAdapter(ObjectMapperFactory objectMapperFactory) {
        this.objectMapper = objectMapperFactory.get();
    }

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception {
        // TODO: RequestParam 적용
//        request.getQueryString()

        // 핸들러 메소드 실행
        Object controller = handler.getBean();
        Method handlerMethod = handler.getMethod();

        // 반환 타입을 JSON 객체로 반환
        return objectMapper.writeValueAsString(handlerMethod.invoke(controller));
    }
}
