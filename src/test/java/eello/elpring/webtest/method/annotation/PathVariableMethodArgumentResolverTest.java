package eello.elpring.webtest.method.annotation;

import eello.elpring.web.bind.annotation.PathVariable;
import eello.elpring.web.bind.convert.*;
import eello.elpring.web.bind.support.TypeConversionService;
import eello.elpring.web.method.MethodParameter;
import eello.elpring.web.method.annotation.PathVariableMethodArgumentResolver;
import eello.elpring.webtest.servlet.FakeHttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PathVariableMethodArgumentResolverTest {

    private PathVariableMethodArgumentResolver resolver;

    @BeforeEach
    void setUp() {
        PrimitiveTypeConverter primitiveTypeConverter = new PrimitiveTypeConverter();
        CustomObjectTypeConverter customObjectTypeConverter = new CustomObjectTypeConverter();

        ScalarTypeConverterManager scalarManager = new ScalarTypeConverterManager();
        scalarManager.addTypeConverter(primitiveTypeConverter);
        scalarManager.addTypeConverter(customObjectTypeConverter);

        CollectionTypeConverterManager collectionManager = new CollectionTypeConverterManager();
        collectionManager.addTypeConverter(new ArrayTypeConverter(scalarManager));
        collectionManager.addTypeConverter(new ListTypeConverter(scalarManager));
        collectionManager.addTypeConverter(new SetTypeConverter(scalarManager));

        List<TypeConverterManager<? extends TypeConverter>> managers = new ArrayList<>();
        managers.add(scalarManager);
        managers.add(collectionManager);

        TypeConversionService conversionService = new TypeConversionService(managers);
        resolver = new PathVariableMethodArgumentResolver(conversionService);
    }

    public void testMethod(@PathVariable("userId") int id, String name, @PathVariable String email) {
        // 테스트용
    }

    @Test
    @DisplayName("@PathVariable 어노테이션이 지정된 파라미터는 supportsParameter가 true를 반환해야 한다.")
    void testSupportsParameter_withAnnotation() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("testMethod", int.class, String.class, String.class);
        
        // @PathVariable("userId") int id
        MethodParameter parameterWithAnnotation = MethodParameter.of(method, method.getParameters()[0], 0);
        assertTrue(resolver.supportsParameter(parameterWithAnnotation));

        // @PathVariable String email
        MethodParameter parameterWithNoValAnnotation = MethodParameter.of(method, method.getParameters()[2], 2);
        assertTrue(resolver.supportsParameter(parameterWithNoValAnnotation));
    }

    @Test
    @DisplayName("@PathVariable 어노테이션이 없는 파라미터는 supportsParameter가 false를 반환해야 한다.")
    void testSupportsParameter_withoutAnnotation() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("testMethod", int.class, String.class, String.class);
        
        // String name
        MethodParameter parameterWithoutAnnotation = MethodParameter.of(method, method.getParameters()[1], 1);
        assertFalse(resolver.supportsParameter(parameterWithoutAnnotation));
    }

    @Test
    @DisplayName("어노테이션에 지정된 경로 변수명으로 request attribute에서 값을 찾아 변환 및 반환해야 한다.")
    void testResolveArgument_withExplicitValue() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("testMethod", int.class, String.class, String.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0); // @PathVariable("userId") int id

        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("userId", "1004");

        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/users/1004")
                .addAttribute(PathVariableMethodArgumentResolver.PATH_VARIABLE_ATTRIBUTE_KEY, pathVariables)
                .build();

        Object result = resolver.resolveArgument(parameter, request, null);

        assertNotNull(result);
        assertEquals(1004, result);
    }

    @Test
    @DisplayName("어노테이션에 변수명이 생략된 경우 파라미터 이름을 기본값으로 사용하여 값을 찾아 변환 및 반환해야 한다.")
    void testResolveArgument_withParameterNameFallback() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("testMethod", int.class, String.class, String.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[2], 2); // @PathVariable String email

        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("email", "test@elpring.com");

        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/users/1004/test@elpring.com")
                .addAttribute(PathVariableMethodArgumentResolver.PATH_VARIABLE_ATTRIBUTE_KEY, pathVariables)
                .build();

        Object result = resolver.resolveArgument(parameter, request, null);

        assertNotNull(result);
        assertEquals("test@elpring.com", result);
    }
}
