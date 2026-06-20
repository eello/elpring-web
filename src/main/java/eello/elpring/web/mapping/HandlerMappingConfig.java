package eello.elpring.web.mapping;

import eello.elpring.di.annotation.Bean;
import eello.elpring.di.annotation.Configuration;
import eello.elpring.web.support.ObjectMapperFactory;

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
