package eello.elpring.web.config;
import eello.elpring.web.bind.support.RequestParamConversionService;
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
            RequestParamConversionService requestParamConversionService
    ) {
        HandlerMethodArgumentResolverComposite resolverComposite = new HandlerMethodArgumentResolverComposite();
        resolverComposite.addArgumentResolver(new ServletRequestMethodArgumentResolver());
        resolverComposite.addArgumentResolver(new ServletResponseMethodArgumentResolver());
        resolverComposite.addArgumentResolver(new RequestParamMethodArgumentResolver(requestParamConversionService));
        return resolverComposite;
    }
}
