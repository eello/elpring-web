package eello.elpring.webtest.mapping;

import eello.elpring.web.annotation.RequestParam;
import eello.elpring.web.core.MethodParameter;
import eello.elpring.web.mapping.PrimitiveTypeConverter;
import eello.elpring.web.mapping.RequestParamMethodArgumentResolver;
import eello.elpring.web.mapping.TypeConverter;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RequestParamMethodArgumentResolverTest {

    private RequestParamMethodArgumentResolver resolver;

    @BeforeEach
    void setUp() {
        TypeConverter primitiveTypeConverter = new PrimitiveTypeConverter();
        resolver = new RequestParamMethodArgumentResolver(List.of(primitiveTypeConverter));
    }

    public void sampleMethod(@RequestParam("age") int age, String name) {
        // 테스트를 위한 더미 메서드
    }

    @Test
    @DisplayName("@RequestParam 어노테이션이 선언된 파라미터는 supportsParameter가 true를 반환해야 한다.")
    void supportsParameter_with_annotation() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("sampleMethod", int.class, String.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        boolean result = resolver.supportsParameter(parameter);

        assertTrue(result);
    }

    @Test
    @DisplayName("@RequestParam 어노테이션이 없는 파라미터는 supportsParameter가 false를 반환해야 한다.")
    void not_supportsParameter_without_annotation() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("sampleMethod", int.class, String.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[1], 1);

        boolean result = resolver.supportsParameter(parameter);

        assertFalse(result);
    }

    @Test
    @DisplayName("캐싱 동작 검증: 동일한 타입에 대해 supportsParameter를 다시 호출하면 true를 반환해야 한다.")
    void supportsParameter_uses_cache() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("sampleMethod", int.class, String.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        // 첫 번째 호출 (캐시에 등록됨)
        assertTrue(resolver.supportsParameter(parameter));
        // 두 번째 호출 (캐시에서 읽어옴)
        assertTrue(resolver.supportsParameter(parameter));
    }

    @Test
    @DisplayName("resolveName을 통해 Request의 파라미터 값을 변환하여 리턴해야 한다.")
    void resolveName_converts_request_parameter() throws NoSuchMethodException {
        // 캐싱을 위해 먼저 supportsParameter를 호출해야 함 (현재 구현의 제약사항)
        Method method = this.getClass().getMethod("sampleMethod", int.class, String.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);
        resolver.supportsParameter(parameter);

        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/")
                .addParameter("age", "25")
                .build();

        Object result = resolver.resolveName("age", parameter, request);

        assertEquals(25, result);
    }
}
