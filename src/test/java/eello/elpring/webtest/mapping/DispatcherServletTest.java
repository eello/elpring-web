package eello.elpring.webtest.mapping;

import eello.elpring.web.DispatcherServlet;
import eello.elpring.web.core.HandlerExecutionChain;
import eello.elpring.web.mapping.HandlerAdapter;
import eello.elpring.web.mapping.HandlerMapping;
import eello.elpring.web.mapping.HandlerMethod;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class DispatcherServletTest {

    private static class TestDispatcherServlet extends DispatcherServlet {
        public TestDispatcherServlet(HandlerMapping handlerMapping, HandlerAdapter handlerAdapter) {
            super(handlerMapping, handlerAdapter);
        }

        @Override
        public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
            super.service(req, res);
        }
    }

    @Test
    void testService_CallsHandlerMappingAndAdapter() throws Exception {
        AtomicBoolean mappingCalled = new AtomicBoolean(false);
        AtomicBoolean adapterCalled = new AtomicBoolean(false);

        HandlerMethod dummyHandler = new HandlerMethod(null, "dummy", null, null);
        HandlerExecutionChain dummyChain = new HandlerExecutionChain(dummyHandler);

        HandlerMapping mockMapping = new HandlerMapping() {
            @Override
            public HandlerExecutionChain getHandler(HttpServletRequest request) {
                mappingCalled.set(true);
                return dummyChain;
            }
        };

        HandlerAdapter mockAdapter = new HandlerAdapter() {
            @Override
            public String handle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
                assertSame(dummyHandler, handler);
                adapterCalled.set(true);
                return "dummyResult";
            }
        };

        TestDispatcherServlet servlet = new TestDispatcherServlet(mockMapping, mockAdapter);
        HttpServletRequest request = FakeHttpServletRequest.of("GET", "/test");
        HttpServletResponse response = FakeHttpServletResponse.of();

        servlet.service(request, response);

        assertTrue(mappingCalled.get(), "HandlerMapping should be called");
        assertTrue(adapterCalled.get(), "HandlerAdapter should be called");
    }

    @Test
    void testService_ThrowsRuntimeExceptionWhenAdapterFails() {
        HandlerMethod dummyHandler = new HandlerMethod(null, "dummy", null, null);
        HandlerExecutionChain dummyChain = new HandlerExecutionChain(dummyHandler);

        HandlerMapping mockMapping = new HandlerMapping() {
            @Override
            public HandlerExecutionChain getHandler(HttpServletRequest request) {
                return dummyChain;
            }
        };

        HandlerAdapter mockAdapter = new HandlerAdapter() {
            @Override
            public String handle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception {
                throw new Exception("Simulated adapter failure");
            }
        };

        TestDispatcherServlet servlet = new TestDispatcherServlet(mockMapping, mockAdapter);
        HttpServletRequest request = FakeHttpServletRequest.of("GET", "/test");
        HttpServletResponse response = FakeHttpServletResponse.of();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            servlet.service(request, response);
        });

        assertEquals("java.lang.Exception: Simulated adapter failure", exception.getMessage());
    }
}
