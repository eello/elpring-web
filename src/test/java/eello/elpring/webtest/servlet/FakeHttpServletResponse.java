package eello.elpring.webtest.servlet;

import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Proxy;

public class FakeHttpServletResponse {

    public static HttpServletResponse of() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        return (HttpServletResponse) Proxy.newProxyInstance(
                HttpServletResponse.class.getClassLoader(),
                new Class<?>[]{HttpServletResponse.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getWriter")) {
                        return printWriter;
                    }
                    return null;
                }
        );
    }
}
