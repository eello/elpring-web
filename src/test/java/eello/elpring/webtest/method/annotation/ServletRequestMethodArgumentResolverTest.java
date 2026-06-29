package eello.elpring.webtest.method.annotation;

import eello.elpring.web.method.MethodParameter;
import eello.elpring.web.method.annotation.ServletRequestMethodArgumentResolver;
import eello.elpring.webtest.servlet.FakeHttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServletRequestMethodArgumentResolverTest {

    private final ServletRequestMethodArgumentResolver resolver = new ServletRequestMethodArgumentResolver();

    @Test
    @DisplayName("HttpServletRequest 파라미터는 지원한다")
    void supportsParameter() throws NoSuchMethodException {
        // given
        Method method = getClass().getDeclaredMethod("testMethod", HttpServletRequest.class, String.class);
        MethodParameter supportedParam = MethodParameter.of(method, method.getParameters()[0], 0);
        MethodParameter unsupportedParam = MethodParameter.of(method, method.getParameters()[1], 1);

        // when & then
        assertTrue(resolver.supportsParameter(supportedParam));
        assertFalse(resolver.supportsParameter(unsupportedParam));
    }

    @Test
    @DisplayName("HttpServletRequest 파라미터에 현재 Request 인스턴스를 반환한다")
    void resolveArgument() throws NoSuchMethodException {
        // given
        Method method = getClass().getDeclaredMethod("testMethod", HttpServletRequest.class, String.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        HttpServletRequest request = FakeHttpServletRequest.builder().build();

        // when
        Object result = resolver.resolveArgument(parameter, request, null);

        // then
        assertSame(request, result, "resolveArgument는 주입된 HttpServletRequest 객체를 그대로 반환해야 합니다.");
    }

    // 테스트용 메서드
    private void testMethod(HttpServletRequest request, String other) {}
}
