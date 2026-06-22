package eello.elpring.web.config;
import eello.elpring.web.bind.support.TypeConversionService;
import eello.elpring.web.method.annotation.ModelAttributeMethodArgumentResolver;
import eello.elpring.web.method.annotation.RequestParamMethodArgumentResolver;
import eello.elpring.web.method.annotation.ServletRequestMethodArgumentResolver;
import eello.elpring.web.method.annotation.ServletResponseMethodArgumentResolver;
import eello.elpring.web.method.support.HandlerMethodArgumentResolverComposite;

import eello.elpring.di.annotation.Bean;
import eello.elpring.di.annotation.Configuration;

@Configuration
public class ArgumentResolverConfig {

    @Bean
    public HandlerMethodArgumentResolverComposite handlerMethodArgumentResolverComposite(
            TypeConversionService typeConversionService
    ) {
        HandlerMethodArgumentResolverComposite resolverComposite = new HandlerMethodArgumentResolverComposite();
        resolverComposite.addArgumentResolver(new ServletRequestMethodArgumentResolver());
        resolverComposite.addArgumentResolver(new ServletResponseMethodArgumentResolver());
        resolverComposite.addArgumentResolver(new RequestParamMethodArgumentResolver(typeConversionService));
        resolverComposite.addArgumentResolver(new ModelAttributeMethodArgumentResolver(typeConversionService));
        return resolverComposite;
    }
}
