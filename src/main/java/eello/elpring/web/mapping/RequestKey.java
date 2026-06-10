package eello.elpring.web.mapping;

import eello.elpring.web.annotation.RequestMethod;

import java.util.Objects;

public class RequestKey {

    private String path;
    private RequestMethod requestMethod;

    public RequestKey(String path, RequestMethod requestMethod) {
        this.path = path;
        this.requestMethod = requestMethod;
    }

    public String getPath() {
        return path;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RequestKey that = (RequestKey) o;
        return Objects.equals(path, that.path) && requestMethod == that.requestMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, requestMethod);
    }

    @Override
    public String toString() {
        return "Endpoint: " + requestMethod.name() + " " + path;
    }
}
