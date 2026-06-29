package eello.elpring.webtest.method.annotation;

import eello.elpring.web.bind.annotation.RequestBody;
import eello.elpring.web.http.MediaType;
import eello.elpring.web.http.converter.HttpMessageConverter;
import eello.elpring.web.http.converter.JacksonJsonHttpMessageConverter;
import eello.elpring.web.http.converter.StringHttpMessageConverter;
import eello.elpring.web.method.MethodParameter;
import eello.elpring.web.method.annotation.RequestBodyMethodArgumentResolver;
import eello.elpring.webtest.servlet.FakeHttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RequestBodyMethodArgumentResolverTest {

    private RequestBodyMethodArgumentResolver resolver;

    @BeforeEach
    void setUp() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new StringHttpMessageConverter());
        converters.add(new JacksonJsonHttpMessageConverter(new ObjectMapper()));

        resolver = new RequestBodyMethodArgumentResolver(converters);
    }

    public static class TestDto {
        private String name;
        private int age;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }

    public void testMethod(
            @RequestBody TestDto dto,
            @RequestBody List<TestDto> dtoList,
            @RequestBody String plainText,
            TestDto noAnnotationDto
    ) {
    }

    @Test
    @DisplayName("@RequestBody 어노테이션이 존재하는 파라미터는 supportsParameter가 true를 반환해야 한다.")
    void testSupportsParameter_withAnnotation() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("testMethod", TestDto.class, List.class, String.class, TestDto.class);

        // @RequestBody TestDto dto
        MethodParameter parameterWithAnnotation = MethodParameter.of(method, method.getParameters()[0], 0);
        assertTrue(resolver.supportsParameter(parameterWithAnnotation));

        // @RequestBody List<TestDto> dtoList
        MethodParameter parameterWithGenericAnnotation = MethodParameter.of(method, method.getParameters()[1], 1);
        assertTrue(resolver.supportsParameter(parameterWithGenericAnnotation));
    }

    @Test
    @DisplayName("@RequestBody 어노테이션이 없는 파라미터는 supportsParameter가 false를 반환해야 한다.")
    void testSupportsParameter_withoutAnnotation() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("testMethod", TestDto.class, List.class, String.class, TestDto.class);

        // TestDto noAnnotationDto
        MethodParameter parameterWithoutAnnotation = MethodParameter.of(method, method.getParameters()[3], 3);
        assertFalse(resolver.supportsParameter(parameterWithoutAnnotation));
    }

    @Test
    @DisplayName("Content-Type이 application/json일 때 JSON 객체 바디를 DTO로 정상 변환해야 한다.")
    void testResolveArgument_jsonToDto() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("testMethod", TestDto.class, List.class, String.class, TestDto.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        String jsonBody = "{\"name\":\"hong\",\"age\":30}";
        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("POST")
                .uri("/test")
                .addHeader("Content-Type", "application/json")
                .body(jsonBody)
                .build();

        Object result = resolver.resolveArgument(parameter, request, null);

        assertNotNull(result);
        assertInstanceOf(TestDto.class, result);
        TestDto dto = (TestDto) result;
        assertEquals("hong", dto.getName());
        assertEquals(30, dto.getAge());
    }

    @Test
    @DisplayName("Content-Type이 application/json일 때 JSON 배열 바디를 List<DTO>로 정상 변환해야 한다.")
    void testResolveArgument_jsonArrayToListDto() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("testMethod", TestDto.class, List.class, String.class, TestDto.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[1], 1);

        String jsonBody = "[{\"name\":\"hong\",\"age\":30},{\"name\":\"lee\",\"age\":25}]";
        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("POST")
                .uri("/test")
                .addHeader("Content-Type", "application/json")
                .body(jsonBody)
                .build();

        Object result = resolver.resolveArgument(parameter, request, null);

        assertNotNull(result);
        assertInstanceOf(List.class, result);
        List<?> list = (List<?>) result;
        assertEquals(2, list.size());

        TestDto dto1 = (TestDto) list.get(0);
        assertEquals("hong", dto1.getName());
        assertEquals(30, dto1.getAge());

        TestDto dto2 = (TestDto) list.get(1);
        assertEquals("lee", dto2.getName());
        assertEquals(25, dto2.getAge());
    }

    @Test
    @DisplayName("Content-Type이 text/plain일 때 문자열 바디를 String으로 정상 변환해야 한다.")
    void testResolveArgument_textToString() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("testMethod", TestDto.class, List.class, String.class, TestDto.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[2], 2);

        String textBody = "Hello Elpring!";
        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("POST")
                .uri("/test")
                .addHeader("Content-Type", "text/plain")
                .body(textBody)
                .build();

        Object result = resolver.resolveArgument(parameter, request, null);

        assertNotNull(result);
        assertEquals("Hello Elpring!", result);
    }

    @Test
    @DisplayName("지원하지 않는 Content-Type이거나 컨버터를 찾을 수 없는 경우 IllegalStateException이 발생해야 한다.")
    void testResolveArgument_unsupportedMediaType_throwsException() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("testMethod", TestDto.class, List.class, String.class, TestDto.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("POST")
                .uri("/test")
                .addHeader("Content-Type", "text/plain") // TestDto는 text/plain을 지원하지 않음 (String만 지원)
                .body("some body")
                .build();

        assertThrows(IllegalStateException.class, () -> {
            resolver.resolveArgument(parameter, request, null);
        });
    }
}
