package eello.elpring.web.config;
import eello.elpring.web.method.support.HandlerMethodArgumentResolverComposite;
import eello.elpring.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import eello.elpring.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import eello.elpring.di.annotation.Bean;
import eello.elpring.di.annotation.Configuration;
import eello.elpring.web.util.ObjectMapperFactory;

@Configuration
public class HandlerMappingConfig {

    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        return new RequestMappingHandlerMapping();
    }

    @Bean
    public ObjectMapperFactory objectMapperFactory() {
        return new ObjectMapperFactory();
    }

    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter(
            ObjectMapperFactory objectMapperFactory,
            HandlerMethodArgumentResolverComposite resolverComposite
    ) {
        return new RequestMappingHandlerAdapter(objectMapperFactory.get(), resolverComposite);
    }
}
