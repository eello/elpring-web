package eello.elpring.web.mappingtest;

import eello.elpring.di.context.AnnotationConfigApplicationContext;
import eello.elpring.web.annotation.RequestMethod;
import eello.elpring.web.core.HandlerExecutionChain;
import eello.elpring.web.mapping.HandlerMapping;
import eello.elpring.web.mapping.HandlerMethod;
import eello.elpring.web.mapping.RequestMappingHandlerMapping;
import eello.elpring.web.mapping.RequestKey;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RequestMappingHandlerMappingTest extends AbstractHandlerMappingTest {

    @Override
    protected HandlerMapping createHandlerMapping() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                "eello.elpring.web.fixtures.mapping",
                "eello.elpring.web.mapping"
        );
        context.refresh();
        return context.getBean(RequestMappingHandlerMapping.class);
    }

    @Test
    void testDuplicateMappingThrowsException() {
        assertThrows(IllegalStateException.class, () -> {
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                    "eello.elpring.web.fixtures.duplicate",
                    "eello.elpring.web.mapping"
            );
            context.refresh();
        });
    }

    @Test
    void testNonControllerBeanIgnored() {
        HttpServletRequest request = FakeHttpServletRequest.of("GET", "/ignored");
        HandlerExecutionChain chain = handlerMapping.getHandler(request);
        assertNotNull(chain);
        assertNull(chain.getHandler(), "Non-controller component mappings should be ignored");
    }

    @SuppressWarnings("unchecked")
    @Test
    void testMappingRegistryContainsExactlyAllExpectedHandlers() throws Exception {
        Field field = RequestMappingHandlerMapping.class.getDeclaredField("mappingRegistry");
        field.setAccessible(true);
        Map<RequestKey, HandlerMethod> mappingRegistry = (Map<RequestKey, HandlerMethod>) field.get(handlerMapping);

        assertNotNull(mappingRegistry, "mappingRegistry should not be null");
        
        // Expected size: 3 (from basic controllers) + 8 (from 2x2x2 FakeMultiMappingController) = 11
        assertEquals(11, mappingRegistry.size(), "mappingRegistry should contain exactly 11 handlers");

        // Verify basic controllers
        assertTrue(mappingRegistry.containsKey(new RequestKey("/hello", RequestMethod.GET)));
        assertTrue(mappingRegistry.containsKey(new RequestKey("/api/users", RequestMethod.POST)));
        assertTrue(mappingRegistry.containsKey(new RequestKey("/api/items", RequestMethod.GET)));

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
}
