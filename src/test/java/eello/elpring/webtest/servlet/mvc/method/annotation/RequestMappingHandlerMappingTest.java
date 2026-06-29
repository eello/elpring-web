package eello.elpring.webtest.servlet.mvc.method.annotation;
import eello.elpring.webtest.servlet.FakeHttpServletRequest;

import eello.elpring.di.context.AnnotationConfigApplicationContext;
import eello.elpring.web.bind.annotation.RequestMethod;
import eello.elpring.web.servlet.HandlerExecutionChain;
import eello.elpring.web.servlet.HandlerMapping;
import eello.elpring.web.method.HandlerMethod;
import eello.elpring.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import eello.elpring.web.servlet.mvc.RequestKey;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.List;
import eello.elpring.web.servlet.mvc.method.annotation.PatternRequestMapping;
import eello.elpring.web.method.annotation.PathVariableMethodArgumentResolver;

import static org.junit.jupiter.api.Assertions.*;

public class RequestMappingHandlerMappingTest extends AbstractHandlerMappingTest {

    @Override
    protected HandlerMapping createHandlerMapping() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                "eello.elpring.webtest.fixtures.mapping",
                "eello.elpring.web"
        );
        context.refresh();
        return context.getBean(RequestMappingHandlerMapping.class);
    }

    @Test
    void testDuplicateMappingThrowsException() {
        assertThrows(IllegalStateException.class, () -> {
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                    "eello.elpring.webtest.fixtures.duplicate",
                    "eello.elpring.web"
            );
            context.refresh();
        });
    }

    @Test
    void testNonControllerBeanIgnored() {
        HttpServletRequest request = FakeHttpServletRequest.of("GET", "/ignored");
        HandlerExecutionChain chain = handlerMapping.getHandler(request);
        assertNull(chain, "Non-controller component mappings should be ignored");
    }

    @SuppressWarnings("unchecked")
    @Test
    void testMappingRegistryContainsExactlyAllExpectedHandlers() throws Exception {
        Field field = RequestMappingHandlerMapping.class.getDeclaredField("staticHandlerRegistry");
        field.setAccessible(true);
        Map<RequestKey, HandlerMethod> mappingRegistry = (Map<RequestKey, HandlerMethod>) field.get(handlerMapping);

        assertNotNull(mappingRegistry, "mappingRegistry should not be null");
        
        // Expected size: 3 (basic) + 8 (multi) + 6 (tomcat test controller endpoints) = 17
        assertEquals(17, mappingRegistry.size(), "mappingRegistry should contain exactly 17 handlers");

        // Verify basic controllers
        assertTrue(mappingRegistry.containsKey(new RequestKey("/hello", RequestMethod.GET)));
        assertTrue(mappingRegistry.containsKey(new RequestKey("/api/users", RequestMethod.POST)));
        assertTrue(mappingRegistry.containsKey(new RequestKey("/api/items", RequestMethod.GET)));

        // Verify tomcat test controller
        assertTrue(mappingRegistry.containsKey(new RequestKey("/test-tomcat", RequestMethod.GET)));
        assertTrue(mappingRegistry.containsKey(new RequestKey("/test-tomcat/primitive", RequestMethod.GET)));
        assertTrue(mappingRegistry.containsKey(new RequestKey("/test-tomcat/dto", RequestMethod.GET)));
        assertTrue(mappingRegistry.containsKey(new RequestKey("/test-tomcat/requestbody", RequestMethod.POST)));
        assertTrue(mappingRegistry.containsKey(new RequestKey("/test-tomcat/requestbody/list", RequestMethod.POST)));
        assertTrue(mappingRegistry.containsKey(new RequestKey("/test-tomcat/servlet-api", RequestMethod.GET)));

        // Verify multi-mapping combinations (2 classPaths * 2 methodPaths * 2 methods = 8 combinations)
        assertTrue(mappingRegistry.containsKey(new RequestKey("/api/v1/users", RequestMethod.GET)));
        assertTrue(mappingRegistry.containsKey(new RequestKey("/api/v1/users", RequestMethod.POST)));
        assertTrue(mappingRegistry.containsKey(new RequestKey("/api/v1/members", RequestMethod.GET)));
        assertTrue(mappingRegistry.containsKey(new RequestKey("/api/v1/members", RequestMethod.POST)));
        
        assertTrue(mappingRegistry.containsKey(new RequestKey("/api/v2/users", RequestMethod.GET)));
        assertTrue(mappingRegistry.containsKey(new RequestKey("/api/v2/users", RequestMethod.POST)));
        assertTrue(mappingRegistry.containsKey(new RequestKey("/api/v2/members", RequestMethod.GET)));
        assertTrue(mappingRegistry.containsKey(new RequestKey("/api/v2/members", RequestMethod.POST)));

        // Verify absence of ignored keys
        assertFalse(mappingRegistry.containsKey(new RequestKey("/ignored", RequestMethod.GET)));
    }

    @Test
    void testCombinePathNormalization() throws Exception {
        Method method = RequestMappingHandlerMapping.class.getDeclaredMethod("combinePath", String.class, String.class);
        method.setAccessible(true);

        // 1. Both empty
        assertEquals("/", method.invoke(handlerMapping, "", ""), "Both empty paths should normalize to /");
        
        // 2. Both only slashes
        assertEquals("/", method.invoke(handlerMapping, "/", "/"), "Both slash-only paths should normalize to /");
        
        // 3. Normal path combine
        assertEquals("/api/users", method.invoke(handlerMapping, "api", "users"), "Normal combine should result in /api/users");
        
        // 4. Combine with starting slashes
        assertEquals("/api/users", method.invoke(handlerMapping, "/api", "/users"), "Starting slashes should combine to /api/users");
        
        // 5. Combine with trailing slashes
        assertEquals("/api/users", method.invoke(handlerMapping, "api/", "users/"), "Trailing slashes should be stripped resulting in /api/users");
        
        // 6. Multiple consecutive slashes
        assertEquals("/api/users", method.invoke(handlerMapping, "///api//", "///users///"), "Multiple slashes should combine and reduce to single /api/users");
        
        // 7. Empty classPath, normal methodPath
        assertEquals("/users", method.invoke(handlerMapping, "", "users"), "Empty classpath with methodpath should result in /users");
        
        // 8. Normal classPath, empty methodPath
        assertEquals("/api", method.invoke(handlerMapping, "api", ""), "Empty methodpath with classpath should result in /api");
        
        // 9. ClassPath with trailing slash, methodPath is single slash
        assertEquals("/api", method.invoke(handlerMapping, "/api/", "/"), "Classpath with slash and empty method path should resolve to /api");
    }

    @SuppressWarnings("unchecked")
    @Test
    void testDynamicMappingRegistryContainsExpectedHandlers() throws Exception {
        Field field = RequestMappingHandlerMapping.class.getDeclaredField("patternHandlerRegistry");
        field.setAccessible(true);
        List<PatternRequestMapping> patternHandlerRegistry = (List<PatternRequestMapping>) field.get(handlerMapping);

        assertNotNull(patternHandlerRegistry, "patternHandlerRegistry should not be null");
        
        // TomcatTestController의 testPathVariable 1개만 매핑되어야 함
        assertEquals(1, patternHandlerRegistry.size(), "patternHandlerRegistry should contain exactly 1 handler");

        PatternRequestMapping route = patternHandlerRegistry.get(0);
        assertEquals(RequestMethod.GET, route.getMethod());
        assertEquals("^/test-tomcat/pathvariable/([^/]+)/orders/([^/]+)$", route.getPattern().pattern());
        assertTrue(route.isMatch("/test-tomcat/pathvariable/123/orders/abc", RequestMethod.GET));
        assertEquals(List.of("id", "orderId"), route.getPathVars());
    }

    @Test
    void testGetHandlerWithPathVariable() {
        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/test-tomcat/pathvariable/100/orders/ord-50")
                .build();

        HandlerExecutionChain chain = handlerMapping.getHandler(request);
        assertNotNull(chain, "Handler chain should be resolved for dynamic path variable URL");
        assertNotNull(chain.getHandler(), "Handler should not be null");

        // Request attribute에 PATH_VARIABLES가 세팅되었는지 확인
        @SuppressWarnings("unchecked")
        Map<String, String> pathVars = (Map<String, String>) request.getAttribute(PathVariableMethodArgumentResolver.PATH_VARIABLE_ATTRIBUTE_KEY);
        assertNotNull(pathVars);
        assertEquals("100", pathVars.get("id"));
        assertEquals("ord-50", pathVars.get("orderId"));
    }
}
