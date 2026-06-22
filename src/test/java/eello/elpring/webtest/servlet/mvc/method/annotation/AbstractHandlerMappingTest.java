package eello.elpring.webtest.servlet.mvc.method.annotation;
import eello.elpring.webtest.servlet.FakeHttpServletRequest;

import eello.elpring.web.servlet.HandlerExecutionChain;
import eello.elpring.web.servlet.HandlerMapping;
import eello.elpring.web.method.HandlerMethod;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractHandlerMappingTest {

    protected HandlerMapping handlerMapping;

    protected abstract HandlerMapping createHandlerMapping();

    @BeforeEach
    void setUp() {
        this.handlerMapping = createHandlerMapping();
    }

    @Test
    void testGetHandler_SimpleGet() throws Exception {
        HttpServletRequest request = FakeHttpServletRequest.of("GET", "/hello");
        HandlerExecutionChain chain = handlerMapping.getHandler(request);

        assertNotNull(chain, "HandlerExecutionChain should not be null");
        HandlerMethod handlerMethod = chain.getHandler();
        assertNotNull(handlerMethod, "HandlerMethod should not be null");

        Method method = getDeclaredField(handlerMethod, "method");
        assertEquals("hello", method.getName());

        Class<?> beanType = getDeclaredField(handlerMethod, "beanType");
        assertEquals("FakeHelloController", beanType.getSimpleName());
    }

    @Test
    void testGetHandler_CombinePath() throws Exception {
        HttpServletRequest request = FakeHttpServletRequest.of("POST", "/api/users");
        HandlerExecutionChain chain = handlerMapping.getHandler(request);

        assertNotNull(chain, "HandlerExecutionChain should not be null");
        HandlerMethod handlerMethod = chain.getHandler();
        assertNotNull(handlerMethod, "HandlerMethod should not be null");

        Method method = getDeclaredField(handlerMethod, "method");
        assertEquals("createUser", method.getName());

        Class<?> beanType = getDeclaredField(handlerMethod, "beanType");
        assertEquals("FakeApiController", beanType.getSimpleName());
    }

    @Test
    void testGetHandler_NormalizedPath() throws Exception {
        HttpServletRequest request = FakeHttpServletRequest.of("GET", "/api/items");
        HandlerExecutionChain chain = handlerMapping.getHandler(request);

        assertNotNull(chain, "HandlerExecutionChain should not be null");
        HandlerMethod handlerMethod = chain.getHandler();
        assertNotNull(handlerMethod, "HandlerMethod should not be null");

        Method method = getDeclaredField(handlerMethod, "method");
        assertEquals("getItems", method.getName());

        Class<?> beanType = getDeclaredField(handlerMethod, "beanType");
        assertEquals("FakeApiController", beanType.getSimpleName());
    }

    @Test
    void testGetHandler_NotFound() {
        HttpServletRequest request = FakeHttpServletRequest.of("GET", "/not-found");
        HandlerExecutionChain chain = handlerMapping.getHandler(request);

        assertNull(chain, "HandlerExecutionChain should be null for unmapped paths");
    }

    @Test
    void testGetHandler_MultiMapping_Get() throws Exception {
        HttpServletRequest request = FakeHttpServletRequest.of("GET", "/api/v1/users");
        HandlerExecutionChain chain = handlerMapping.getHandler(request);

        assertNotNull(chain);
        HandlerMethod handlerMethod = chain.getHandler();
        assertNotNull(handlerMethod);

        Method method = getDeclaredField(handlerMethod, "method");
        assertEquals("getOrPostUsers", method.getName());

        Class<?> beanType = getDeclaredField(handlerMethod, "beanType");
        assertEquals("FakeMultiMappingController", beanType.getSimpleName());
    }

    @Test
    void testGetHandler_MultiMapping_Post() throws Exception {
        HttpServletRequest request = FakeHttpServletRequest.of("POST", "/api/v2/members");
        HandlerExecutionChain chain = handlerMapping.getHandler(request);

        assertNotNull(chain);
        HandlerMethod handlerMethod = chain.getHandler();
        assertNotNull(handlerMethod);

        Method method = getDeclaredField(handlerMethod, "method");
        assertEquals("getOrPostUsers", method.getName());

        Class<?> beanType = getDeclaredField(handlerMethod, "beanType");
        assertEquals("FakeMultiMappingController", beanType.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    private <T> T getDeclaredField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(obj);
    }
}
