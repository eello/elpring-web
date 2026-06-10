package eello.elpring.web.annotation;

public enum RequestMethod {
    GET, POST, PUT, DELETE,
    ;

    public static RequestMethod resolve(String method) {
        if (method == null || method.isBlank()) {
            return null;
        }

        try {
            return valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
