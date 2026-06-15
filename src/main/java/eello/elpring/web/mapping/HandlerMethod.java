package eello.elpring.web.mapping;

import eello.elpring.web.core.MethodParameter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class HandlerMethod {

    private Method method;
    private String beanName;
    private Object bean;
    private Class<?> beanType;
    private MethodParameter[] parameters;

    public HandlerMethod(Method method, String beanName, Object bean, Class<?> beanType) {
        this.method = method;
        this.beanName = beanName;
        this.bean = bean;
        this.beanType = beanType;

        parameters = new MethodParameter[method.getParameterCount()];
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = method.getParameters()[i];
            parameters[i] = MethodParameter.of(method, param, i);
        }
    }

    public Method getMethod() {
        return method;
    }

    public String getBeanName() {
        return beanName;
    }

    public Object getBean() {
        return bean;
    }

    public Class<?> getBeanType() {
        return beanType;
    }
}
