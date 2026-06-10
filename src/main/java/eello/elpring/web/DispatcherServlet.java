package eello.elpring.web;

import eello.elpring.di.annotation.Component;
import eello.elpring.di.context.ApplicationContext;
import eello.elpring.di.context.ApplicationContextAware;
import eello.elpring.di.exception.BeansException;
import eello.elpring.web.core.HandlerExecutionChain;
import eello.elpring.web.mapping.HandlerAdapter;
import eello.elpring.web.mapping.HandlerMapping;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class DispatcherServlet extends HttpServlet implements ApplicationContextAware {

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
            HandlerExecutionChain mappedHandler = handlerMapping.getHandler(req);
            if (mappedHandler == null) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String result = handlerAdapter.handle(req, res, mappedHandler.getHandler());
            res.setContentType("application/json");
            res.getWriter().write(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
