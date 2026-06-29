package eello.elpring.webtest.method.annotation;

import eello.elpring.web.method.MethodParameter;
import eello.elpring.web.method.annotation.ServletResponseMethodArgumentResolver;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServletResponseMethodArgumentResolverTest {

    private final ServletResponseMethodArgumentResolver resolver = new ServletResponseMethodArgumentResolver();

    @Test
    @DisplayName("HttpServletResponse 파라미터는 지원한다")
    void supportsParameter() throws NoSuchMethodException {
        // given
        Method method = getClass().getDeclaredMethod("testMethod", HttpServletResponse.class, String.class);
        MethodParameter supportedParam = MethodParameter.of(method, method.getParameters()[0], 0);
        MethodParameter unsupportedParam = MethodParameter.of(method, method.getParameters()[1], 1);

        // when & then
        assertTrue(resolver.supportsParameter(supportedParam));
        assertFalse(resolver.supportsParameter(unsupportedParam));
    }

    @Test
    @DisplayName("HttpServletResponse 파라미터에 현재 Response 인스턴스를 반환한다")
    void resolveArgument() throws NoSuchMethodException {
        // given
        Method method = getClass().getDeclaredMethod("testMethod", HttpServletResponse.class, String.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        HttpServletResponse response = (HttpServletResponse) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{HttpServletResponse.class},
                (proxy, method1, args) -> null
        );

        // when
        Object result = resolver.resolveArgument(parameter, null, response);

        // then
        assertSame(response, result, "resolveArgument는 주입된 HttpServletResponse 객체를 그대로 반환해야 합니다.");
    }

    // 테스트용 메서드
    private void testMethod(HttpServletResponse response, String other) {}
}
