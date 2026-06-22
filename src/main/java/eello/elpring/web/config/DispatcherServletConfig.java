package eello.elpring.web.config;
import eello.elpring.web.servlet.DispatcherServlet;
import eello.elpring.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import eello.elpring.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import eello.elpring.di.annotation.Bean;
import eello.elpring.di.annotation.Configuration;

@Configuration
public class DispatcherServletConfig {

    @Bean
    public DispatcherServlet dispatcherServlet(
            RequestMappingHandlerMapping handlerMapping,
            RequestMappingHandlerAdapter handlerAdapter
    ) {
        return new DispatcherServlet(handlerMapping, handlerAdapter);
    }
}
