package eello.elpring.webtest.method.annotation;
import eello.elpring.webtest.servlet.FakeHttpServletRequest;
import eello.elpring.web.bind.convert.ArrayTypeConverter;
import eello.elpring.web.bind.convert.CollectionTypeConverterManager;
import eello.elpring.web.bind.convert.CustomObjectTypeConverter;
import eello.elpring.web.bind.convert.ListTypeConverter;
import eello.elpring.web.bind.convert.PrimitiveTypeConverter;
import eello.elpring.web.bind.convert.ScalarTypeConverterManager;
import eello.elpring.web.bind.convert.SetTypeConverter;
import eello.elpring.web.bind.convert.TypeConverter;
import eello.elpring.web.bind.convert.TypeConverterManager;
import eello.elpring.web.bind.support.TypeConversionService;
import eello.elpring.web.method.annotation.RequestParamMethodArgumentResolver;
import eello.elpring.web.exception.MissingServletRequestParameterException;

import eello.elpring.web.bind.annotation.RequestParam;
import eello.elpring.web.method.MethodParameter;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RequestParamMethodArgumentResolverTest {

    private RequestParamMethodArgumentResolver resolver;
    private TypeConversionService conversionService;

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
        
        java.util.List<TypeConverterManager<? extends TypeConverter>> managers = new java.util.ArrayList<>();
        managers.add(scalarManager);
        managers.add(collectionManager);
        
        conversionService = new TypeConversionService(managers);
        resolver = new RequestParamMethodArgumentResolver(conversionService);
    }

    public void sampleMethod(@RequestParam("age") int age, HttpServletRequest unsupport) {
        // 테스트용
    }

    public void sampleMethodList(@RequestParam("ids") List<Integer> ids) {
        // 테스트용
    }

    public void sampleMethodSet(@RequestParam("codes") Set<Integer> codes) {
        // 테스트용
    }

    public void sampleRequiredTrue(@RequestParam("age") int age) {
    }

    public void sampleRequiredFalse(@RequestParam(value = "age", required = false) Integer age) {
    }

    public void sampleRequiredFalsePrimitive(@RequestParam(value = "age", required = false) int age) {
    }

    public void sampleNoAnnotation(Integer age) {
    }

    public void sampleNoAnnotationPrimitive(int age) {
    }

    @Test
    @DisplayName("@RequestParam 어노테이션이 선언된 파라미터는 supportsParameter가 true를 반환해야 한다.")
    void supportsParameter_with_annotation() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("sampleMethod", int.class, HttpServletRequest.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        boolean result = resolver.supportsParameter(parameter);

        assertTrue(result);
    }

    @Test
    @DisplayName("리졸버가 지원하지 않는 파라미터 타입인 경우 supportsParameter가 false를 반환해야 한다.")
    void not_supportsParameter_without_annotation() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("sampleMethod", int.class, HttpServletRequest.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[1], 1);

        boolean result = resolver.supportsParameter(parameter);

        assertFalse(result);
    }

    @Test
    @DisplayName("resolveName을 통해 List<Integer> 파라미터 값을 정상적으로 변환하여 리턴해야 한다.")
    void resolveArgument_converts_request_parameter_to_list() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("sampleMethodList", List.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);
        resolver.supportsParameter(parameter);

        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/")
                .addParameter("ids", "1", "2", "3")
                .build();

        Object result = resolver.resolveArgument(parameter, request, null);

        assertInstanceOf(List.class, result);
        List<?> list = (List<?>) result;
        assertEquals(3, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }

    @Test
    @DisplayName("resolveName을 통해 Set<Integer> 파라미터 값을 정상적으로 변환하여 리턴해야 한다.")
    void resolveArgument_converts_request_parameter_to_set() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("sampleMethodSet", Set.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);
        resolver.supportsParameter(parameter);

        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/")
                .addParameter("codes", "10", "20", "20", "30")
                .build();

        Object result = resolver.resolveArgument(parameter, request, null);

        assertInstanceOf(Set.class, result);
        Set<?> set = (Set<?>) result;
        assertEquals(3, set.size());
        assertTrue(set.contains(10));
        assertTrue(set.contains(20));
        assertTrue(set.contains(30));
    }

    @Test
    @DisplayName("required = true일 때 파라미터가 없으면 MissingServletRequestParameterException이 발생해야 한다.")
    void resolveArgument_throwsException_when_requiredParamIsMissing() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("sampleRequiredTrue", int.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/")
                .build();

        assertThrows(MissingServletRequestParameterException.class, () ->
                resolver.resolveArgument(parameter, request, null)
        );
    }

    @Test
    @DisplayName("required = false일 때 파라미터가 없으면 null을 리턴해야 한다.")
    void resolveArgument_returnsNull_when_optionalParamIsMissing() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("sampleRequiredFalse", Integer.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/")
                .build();

        Object result = resolver.resolveArgument(parameter, request, null);

        assertNull(result);
    }

    @Test
    @DisplayName("required = false이고 프리미티브 타입일 때 파라미터가 없으면 IllegalStateException이 발생해야 한다.")
    void resolveArgument_throwsException_when_optionalPrimitiveParamIsMissing() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("sampleRequiredFalsePrimitive", int.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/")
                .build();

        assertThrows(IllegalStateException.class, () ->
                resolver.resolveArgument(parameter, request, null)
        );
    }

    @Test
    @DisplayName("어노테이션이 없고 required가 아닐 때 파라미터가 없으면 null을 리턴해야 한다.")
    void resolveArgument_returnsNull_when_noAnnotationParamIsMissing() throws NoSuchMethodException {
        RequestParamMethodArgumentResolver defaultResolver = new RequestParamMethodArgumentResolver(conversionService, true);
        Method method = this.getClass().getMethod("sampleNoAnnotation", Integer.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/")
                .build();

        Object result = defaultResolver.resolveArgument(parameter, request, null);

        assertNull(result);
    }

    @Test
    @DisplayName("어노테이션이 없고 프리미티브 타입일 때 파라미터가 없으면 IllegalStateException이 발생해야 한다.")
    void resolveArgument_throwsException_when_noAnnotationPrimitiveParamIsMissing() throws NoSuchMethodException {
        RequestParamMethodArgumentResolver defaultResolver = new RequestParamMethodArgumentResolver(conversionService, true);
        Method method = this.getClass().getMethod("sampleNoAnnotationPrimitive", int.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/")
                .build();

        assertThrows(IllegalStateException.class, () ->
                defaultResolver.resolveArgument(parameter, request, null)
        );
    }
}
