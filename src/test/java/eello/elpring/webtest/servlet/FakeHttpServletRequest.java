package eello.elpring.webtest.servlet;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class FakeHttpServletRequest {

    public static Builder builder() {
        return new Builder();
    }

    public static HttpServletRequest of(String method, String uri) {
        return builder().method(method).uri(uri).build();
    }

    public static class Builder {
        private String method = "GET";
        private String uri = "/";
        private final Map<String, String[]> parameters = new HashMap<>();

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder addParameter(String name, String... values) {
            this.parameters.put(name, values);
            return this;
        }

        public HttpServletRequest build() {
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
                        if (proxyMethod.getName().equals("getParameterValues") && args != null && args.length == 1) {
                            return parameters.get((String) args[0]);
                        }
                        return null;
                    }
            );
        }
    }
}
