package eello.elpring.web.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class MethodParameter {

    private Executable executable;
    private int parameterIndex;
    private String parameterName;
    private Class<?> parameterType;
    private Parameter parameter;
    private Annotation[] parameterAnnotations;

    public static MethodParameter of(Method method, Parameter parameter, int parameterIndex) {
        MethodParameter mp = new MethodParameter();
        mp.executable = method;
        mp.parameterIndex = parameterIndex;
        mp.parameterName = parameter.getName();
        mp.parameterType = parameter.getType();
        mp.parameter = parameter;
        mp.parameterAnnotations = parameter.getAnnotations();

        return mp;
    }

    public Executable getExecutable() {
        return executable;
    }

    public int getParameterIndex() {
        return parameterIndex;
    }

    public String getParameterName() {
        return parameterName;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public Annotation[] getParameterAnnotations() {
        return parameterAnnotations;
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return parameter.getAnnotation(annotationClass);
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return parameter.isAnnotationPresent(annotationClass);
    }
}
