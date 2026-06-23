package eello.elpring.web.servlet.mvc.method.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteUtils {

    private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\{([^/]+)\\}");

    private RouteUtils() {
        throw new AssertionError("Utility class는 인스턴스로 생성할 수 없음.");
    }

    /**
     * 입력된 URL 경로에 @PathVariable 매핑용 중괄호가 포함되어 있는지 검사
     * @param path 검사할 경로 (예: "/users/{id}", "/home")
     * @return 포함되어 있으면 true, 쌩 문자열 경로면 false
     */
    public static boolean hasPathVariable(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        // 🔍 정규식 패턴과 매처를 돌려 하나라도 일치하는 중괄호 쌍이 있는지 검사
        return PATH_VARIABLE_PATTERN.matcher(path).find();
    }

    /**
     * path에서 PathVariable의 변수명을 뽑아 리턴
     * ex) "/products/{category}/{productId}" -> return ["category", "productId"]
     */
    public static List<String> extractVariableNames(String path) {
        List<String> varNames = new ArrayList<>();
        if (path == null || path.isEmpty()) {
            return varNames;
        }

        Matcher matcher = PATH_VARIABLE_PATTERN.matcher(path);

        // 🔍 경로 전체를 돌면서 중괄호 패턴을 계속 찾아냅니다 (find)
        while (matcher.find()) {
            // matcher.group(1)을 호출하면 껍데기 {}를 제외하고
            // 첫 번째 캡처 그룹인 ([^/]+) 내부의 '알맹이 글자'만 추출
            String varName = matcher.group(1);
            varNames.add(varName);
        }

        return varNames;
    }

    /**
     * 사용자가 입력한 @PathVariable 경로를 실제 매칭용 자바 Pattern 객체로 변환합
     * @param path 사용자가 등록한 경로 (예: "/users/{id}")
     * @return 컴파일된 자바 Pattern 객체 (예: ^/users/([^/]+)$ 패턴)
     */
    public static Pattern convertToRegexPattern(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        // 중괄호와 그 안의 내용(\{[^/]+\})을 찾아서
        // 자바 정규식의 캡처 그룹 문자열("([^/]+)")로 통째로 치환
        String regexPath = path.replaceAll("\\{[^/]+\\}", "([^/]+)");

        // 앞뒤 방어선(^, $)을 두르고 정규식 패턴 객체로 빌드하여 반환
        return Pattern.compile("^" + regexPath + "$");
    }
}
