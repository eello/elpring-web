package eello.elpring.web.servlet.mvc.method.annotation;

import eello.elpring.web.bind.annotation.RequestMethod;
import eello.elpring.web.method.HandlerMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternRequestMapping {

    private Pattern pattern;
    private RequestMethod method;
    private HandlerMethod handlerMethod;
    private List<String> pathVars;

    public PatternRequestMapping(Pattern pattern, RequestMethod method, HandlerMethod handlerMethod, List<String> pathVars) {
        this.pattern = pattern;
        this.method = method;
        this.handlerMethod = handlerMethod;
        this.pathVars = pathVars;
    }

    public static PatternRequestMapping of(RequestMethod method, String path, HandlerMethod handlerMethod) {
        Pattern pattern = RouteUtils.convertToRegexPattern(path);
        List<String> pathVars = RouteUtils.extractVariableNames(path);

        return new PatternRequestMapping(pattern, method, handlerMethod, pathVars);
    }

    public static PatternRequestMapping of(RequestMethod method, String path) {
        return of(method, path, null);
    }

    public Map<String, String> extractPathVariables(String path) {
        Map<String, String> resultMap = new HashMap<>();

        Matcher matcher = this.pattern.matcher(path);

        // 앞서 isMatch()를 통과했으므로 반드시 matches()는 true
        if (matcher.matches()) {
            // 변수명 리스트를 순회하며 정규식 캡처 그룹과 1:1 매핑
            for (int i = 0; i < pathVars.size(); i++) {
                String varName = pathVars.get(i); // 예: "category"

                // 정규식의 group 인덱스는 0번(전체문자열)이 아니라 1번부터 시작하므로 i + 1
                String varValue = matcher.group(i + 1); // 예: "books"

                resultMap.put(varName, varValue);
            }
        }

        return resultMap;
    }

    /**
     * URL 패턴과 HTTP 메서드가 '둘 다' 일치하는지 확인
     */
    public boolean isMatch(String requestUrl, RequestMethod method) {
        // HTTP 메서드가 다르면 정규식은 검사해 볼 필요도 없이 탈락
        if (this.method != method) {
            return false;
        }
        // 메서드가 일치하면, 정규식이 맞는지 검사
        return this.pattern.matcher(requestUrl).matches();
    }

    public void setHandlerMethod(HandlerMethod handlerMethod) {
        this.handlerMethod = handlerMethod;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public HandlerMethod getHandlerMethod() {
        return handlerMethod;
    }

    public List<String> getPathVars() {
        return pathVars;
    }
}
