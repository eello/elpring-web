package eello.elpring.web.mapping;

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
