package eello.elpring.web.mapping;

import eello.elpring.di.annotation.Bean;
import eello.elpring.di.annotation.Configuration;
import eello.elpring.web.DispatcherServlet;

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
