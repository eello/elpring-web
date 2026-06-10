package eello.elpring.webtest.mapping;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Proxy;

public class FakeHttpServletRequest {

    public static HttpServletRequest of(String method, String uri) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, proxyMethod, args) -> {
                    if (proxyMethod.getName().equals("getMethod")) {
                        return method;
                    }
                    if (proxyMethod.getName().equals("getRequestURI")) {
                        return uri;
                    }
                    return null;
                }
        );
    }
}
