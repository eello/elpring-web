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
        private String body = "";
        private final Map<String, String[]> parameters = new HashMap<>();
        private final Map<String, Object> attributes = new HashMap<>();
        private final Map<String, String> headers = new HashMap<>();

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder addParameter(String name, String... values) {
            this.parameters.put(name, values);
            return this;
        }

        public Builder addAttribute(String name, Object value) {
            this.attributes.put(name, value);
            return this;
        }

        public Builder addHeader(String name, String value) {
            this.headers.put(name, value);
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
                        if (proxyMethod.getName().equals("setAttribute") && args != null && args.length == 2) {
                            attributes.put((String) args[0], args[1]);
                            return null;
                        }
                        if (proxyMethod.getName().equals("getAttribute") && args != null && args.length == 1) {
                            return attributes.get((String) args[0]);
                        }
                        if (proxyMethod.getName().equals("getHeader") && args != null && args.length == 1) {
                            return headers.get((String) args[0]);
                        }
                        if (proxyMethod.getName().equals("getReader")) {
                            return new java.io.BufferedReader(new java.io.StringReader(body));
                        }
                        return null;
                    }
            );
        }
    }
}
