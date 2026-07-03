package eello.elpring.web.servlet;
import eello.elpring.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import eello.elpring.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import eello.elpring.di.context.ApplicationContext;
import eello.elpring.di.context.ApplicationContextAware;
import eello.elpring.di.exception.BeansException;
import eello.elpring.web.servlet.HandlerExecutionChain;
import eello.elpring.web.servlet.HandlerAdapter;
import eello.elpring.web.servlet.HandlerMapping;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispatcherServlet extends HttpServlet implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

    private final HandlerMapping handlerMapping; // RequestMappingHandlerMapping
    private final HandlerAdapter handlerAdapter; // RequestMappingHandlerAdapter

    private ApplicationContext applicationContext;

    public DispatcherServlet(HandlerMapping handlerMapping, HandlerAdapter handlerAdapter) {
        this.handlerMapping = handlerMapping;
        this.handlerAdapter = handlerAdapter;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        getServletContext();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doDispatch(req, res);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Dispatching request for [{}] {}", req.getMethod(), req.getRequestURI());
            }

            HandlerExecutionChain mappedHandler = handlerMapping.getHandler(req);
            if (mappedHandler == null) {
                log.warn("No handler found for [{}] {}", req.getMethod(), req.getRequestURI());
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (log.isDebugEnabled()) {
                log.debug("Mapped to handler: {}", mappedHandler.getHandler());
            }

            String result = handlerAdapter.handle(req, res, mappedHandler.getHandler());
            res.setContentType("application/json");
            res.getWriter().write(result);
        } catch (Exception e) {
            log.error("Failed to dispatch request [{}] {}", req.getMethod(), req.getRequestURI(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
