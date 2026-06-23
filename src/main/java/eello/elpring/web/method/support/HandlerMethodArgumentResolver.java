package eello.elpring.web.method.support;

import eello.elpring.web.bind.annotation.PathVariable;
import eello.elpring.web.method.MethodParameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.annotation.Annotation;
import java.util.function.Function;

public interface HandlerMethodArgumentResolver {

    boolean supportsParameter(MethodParameter parameter);

    Object resolveArgument(MethodParameter methodParameter, HttpServletRequest request,
                           HttpServletResponse response);

    default <A extends Annotation> String resolveAnnotatedParameterName(MethodParameter methodParameter,
                                                                        Class<A> annotationType,
                                                                        Function<A, String> valueExtractor) {
        String paramName = methodParameter.getParameterName();

        A annotation = methodParameter.getAnnotation(annotationType);
        if (annotation != null) {
            String annoValue = valueExtractor.apply(annotation);
            if (annoValue != null && !annoValue.isBlank()) {
                paramName = annoValue;
            }
        }

        if (paramName == null || paramName.startsWith("arg")) {
            throw new IllegalArgumentException(
                    "Name for argument of type [" + methodParameter.getParameterType().getName() +
                            "] not specified, and parameter name information not available via reflection. " +
                            "이름을 알 수 없으니 컴파일 시 -parameters 옵션을 켜거나 @RequestParam에 이름을 명시해주세요."
            );
        }

        return paramName;
    }
}
