package eello.elpring.web.mapping;

import eello.elpring.web.core.MethodParameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlerMethodArgumentResolverComposite implements ArgumentResolver {

    private final List<ArgumentResolver> argumentResolvers;
    private final Map<MethodParameter, ArgumentResolver> argumentResolverMap = new HashMap<>();

    public HandlerMethodArgumentResolverComposite() {
        this(new ArrayList<>());
    }

    public HandlerMethodArgumentResolverComposite(List<ArgumentResolver> argumentResolvers) {
        this.argumentResolvers = argumentResolvers;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (argumentResolverMap.containsKey(parameter)) {
            return true;
        }

        findArgumentResolver(parameter);
        return argumentResolverMap.containsKey(parameter);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, HttpServletRequest request,
                                  HttpServletResponse response) {
        ArgumentResolver resolver = findArgumentResolver(methodParameter);
        if (resolver == null) {
            throw new IllegalStateException("No argumentResolver found for method parameter " + methodParameter);
        }


        return resolver.resolveArgument(methodParameter, request, response);
    }

    private ArgumentResolver findArgumentResolver(MethodParameter methodParameter) {
        ArgumentResolver resolver = argumentResolvers.stream().filter(r -> r.supportsParameter(methodParameter))
                .findFirst()
                .orElse(null);

        if (resolver != null) {
            argumentResolverMap.put(methodParameter, resolver);
        }

        return resolver;
    }

    public void addArgumentResolver(ArgumentResolver argumentResolver) {
        argumentResolvers.add(argumentResolver);
    }
}
