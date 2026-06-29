package eello.elpring.web.config;

import eello.elpring.web.bind.support.TypeConversionService;
import eello.elpring.web.http.converter.HttpMessageConverter;
import eello.elpring.web.http.converter.JacksonJsonHttpMessageConverter;
import eello.elpring.web.method.annotation.RequestBodyMethodArgumentResolver;
import eello.elpring.web.http.converter.StringHttpMessageConverter;
import eello.elpring.web.method.annotation.PathVariableMethodArgumentResolver;
import eello.elpring.web.method.annotation.ModelAttributeMethodArgumentResolver;
import eello.elpring.web.method.annotation.RequestParamMethodArgumentResolver;
import eello.elpring.web.method.annotation.ServletRequestMethodArgumentResolver;
import eello.elpring.web.method.annotation.ServletResponseMethodArgumentResolver;
import eello.elpring.web.method.support.HandlerMethodArgumentResolverComposite;

import eello.elpring.di.annotation.Bean;
import eello.elpring.di.annotation.Configuration;
import eello.elpring.web.util.ObjectMapperFactory;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ArgumentResolverConfig {

    @Bean
    public List<HttpMessageConverter<?>> messageConverters(ObjectMapperFactory objectMapperFactory) {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new StringHttpMessageConverter());
        converters.add(new JacksonJsonHttpMessageConverter(objectMapperFactory.get()));
        return converters;
    }

    @Bean
    public HandlerMethodArgumentResolverComposite handlerMethodArgumentResolverComposite(
            TypeConversionService typeConversionService,
            List<HttpMessageConverter<?>> messageConverters
    ) {
        HandlerMethodArgumentResolverComposite resolverComposite = new HandlerMethodArgumentResolverComposite();
        resolverComposite.addArgumentResolver(new PathVariableMethodArgumentResolver(typeConversionService));
        resolverComposite.addArgumentResolver(new RequestParamMethodArgumentResolver(typeConversionService));
        resolverComposite.addArgumentResolver(new RequestBodyMethodArgumentResolver(messageConverters));
        resolverComposite.addArgumentResolver(new ModelAttributeMethodArgumentResolver(typeConversionService));

        resolverComposite.addArgumentResolver(new ServletRequestMethodArgumentResolver());
        resolverComposite.addArgumentResolver(new ServletResponseMethodArgumentResolver());

        resolverComposite.addArgumentResolver(new RequestParamMethodArgumentResolver(typeConversionService, true));
        resolverComposite.addArgumentResolver(new ModelAttributeMethodArgumentResolver(typeConversionService, true));
        return resolverComposite;
    }
}
