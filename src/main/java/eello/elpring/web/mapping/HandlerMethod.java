package eello.elpring.web.mapping;

import java.lang.reflect.Method;

public class HandlerMethod {

    private Method method;
    private String beanName;
    private Object bean;
    private Class<?> beanType;

    public HandlerMethod(Method method, String beanName, Object bean, Class<?> beanType) {
        this.method = method;
        this.beanName = beanName;
        this.bean = bean;
        this.beanType = beanType;
    }
}
