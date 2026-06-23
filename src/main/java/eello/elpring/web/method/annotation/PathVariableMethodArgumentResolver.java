package eello.elpring.web.method.annotation;

import eello.elpring.web.bind.annotation.PathVariable;
import eello.elpring.web.bind.support.TypeConversionService;
import eello.elpring.web.method.MethodParameter;
import eello.elpring.web.method.support.HandlerMethodArgumentResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public class PathVariableMethodArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String PATH_VARIABLE_ATTRIBUTE_KEY = "PATH_VARIABLES";

    private final TypeConversionService typeConversionService;

    public PathVariableMethodArgumentResolver(TypeConversionService typeConversionService) {
        this.typeConversionService = typeConversionService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.isAnnotationPresent(PathVariable.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, HttpServletRequest request,
                                  HttpServletResponse response) {
        String pathVarName = resolveAnnotatedParameterName(methodParameter, PathVariable.class, PathVariable::value);
        Map<String, String> pathVarResultMap = (Map<String, String>) request.getAttribute(PATH_VARIABLE_ATTRIBUTE_KEY);

        Class<?> paramType = methodParameter.getParameterType();
        String rawValue = pathVarResultMap.get(pathVarName);

        return typeConversionService.convert(paramType, new String[]{rawValue});
    }
}
