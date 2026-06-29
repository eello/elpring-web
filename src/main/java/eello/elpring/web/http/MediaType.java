package eello.elpring.web.http;

public enum MediaType {

    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html"),
    APPLICATION_JSON("application/json"),
    ;

    private String value;

    MediaType(String value) {
        this.value = value;
    }

    public static MediaType from(String contentTypeHeader) {
        if (contentTypeHeader == null) {
            return APPLICATION_JSON; // 헤더가 없으면 기본값(Fallback) 세팅
        }

        String cleanHeader = contentTypeHeader.trim().toLowerCase();

        for (MediaType type : values()) {
            if (cleanHeader.startsWith(type.value)) {
                return type;
            }
        }

        // 매칭되는 게 없으면 기본값 혹은 예외 처리
        return APPLICATION_JSON;
    }
}
