package eello.elpring.webtest.method.annotation;

import eello.elpring.web.bind.annotation.ModelAttribute;
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
import eello.elpring.web.exception.MethodArgumentTypeMismatchException;
import eello.elpring.web.method.MethodParameter;
import eello.elpring.web.method.annotation.ModelAttributeMethodArgumentResolver;
import eello.elpring.webtest.servlet.FakeHttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelAttributeMethodArgumentResolverTest {

    private ModelAttributeMethodArgumentResolver resolver;
    private ModelAttributeMethodArgumentResolver fallbackResolver;

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
        resolver = new ModelAttributeMethodArgumentResolver(conversionService);
        fallbackResolver = new ModelAttributeMethodArgumentResolver(conversionService, true);
    }

    // --- 테스트용 DTO 및 인터페이스들 ---
    public static class TestDto {
        private String name;
        private int age;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }

    public static class TestPrivateConstructorDto {
        private String name;
        private int age;

        private TestPrivateConstructorDto() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }

    public static class TestNoDefaultConstructorDto {
        private String name;
        public TestNoDefaultConstructorDto(String name) { this.name = name; }
    }

    public static abstract class TestAbstractDto {
        public TestAbstractDto() {}
    }

    public interface TestInterfaceDto {
    }

    public static class TestUnsupportedFieldDto {
        private Runnable runnable; // 변환을 지원하지 않는 인터페이스 타입
        public void setRunnable(Runnable runnable) { this.runnable = runnable; }
    }

    // --- 테스트용 메서드들 ---
    public void methodWithModelAttribute(@ModelAttribute TestDto testDto) {}
    public void methodWithDtoWithoutAnnotation(TestDto testDto) {}
    public void methodWithInterfaceWithoutAnnotation(TestInterfaceDto dto) {}
    public void methodWithPrimitiveWithoutAnnotation(int age) {}
    public void methodWithStringWithoutAnnotation(String name) {}
    public void methodWithPrivateConstructor(TestPrivateConstructorDto dto) {}
    public void methodWithNoDefaultConstructor(TestNoDefaultConstructorDto dto) {}
    public void methodWithAbstractClass(TestAbstractDto dto) {}
    public void methodWithUnsupportedField(TestUnsupportedFieldDto dto) {}

    // --- supportsParameter 테스트 케이스 ---

    @Test
    @DisplayName("@ModelAttribute 어노테이션이 존재하는 파라미터는 supportsParameter가 true를 반환해야 한다.")
    void supportsParameter_withAnnotation() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("methodWithModelAttribute", TestDto.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        assertTrue(resolver.supportsParameter(parameter));
        assertTrue(fallbackResolver.supportsParameter(parameter));
    }

    @Test
    @DisplayName("어노테이션이 없으나 일반 클래스 타입인 파라미터는 supportsParameter가 fallbackResolver에서만 true를 반환해야 한다.")
    void supportsParameter_withoutAnnotation_generalClass() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("methodWithDtoWithoutAnnotation", TestDto.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        assertFalse(resolver.supportsParameter(parameter));
        assertTrue(fallbackResolver.supportsParameter(parameter));
    }

    @Test
    @DisplayName("어노테이션이 없고 인터페이스 타입인 파라미터는 supportsParameter가 fallbackResolver에서만 true를 반환해야 한다.")
    void supportsParameter_withoutAnnotation_interface() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("methodWithInterfaceWithoutAnnotation", TestInterfaceDto.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        assertFalse(resolver.supportsParameter(parameter));
        assertTrue(fallbackResolver.supportsParameter(parameter));
    }

    @Test
    @DisplayName("어노테이션이 없고 원시 타입인 파라미터는 supportsParameter가 false를 반환해야 한다.")
    void supportsParameter_withoutAnnotation_primitive() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("methodWithPrimitiveWithoutAnnotation", int.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        assertFalse(resolver.supportsParameter(parameter));
    }

    @Test
    @DisplayName("어노테이션이 없고 String 타입인 파라미터는 supportsParameter가 false를 반환해야 한다.")
    void supportsParameter_withoutAnnotation_string() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("methodWithStringWithoutAnnotation", String.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        assertFalse(resolver.supportsParameter(parameter));
    }

    // --- resolveArgument 테스트 케이스 ---

    @Test
    @DisplayName("public 기본 생성자를 가진 DTO의 인스턴스를 생성하고 쿼리스트링 파라미터를 정상적으로 바인딩해야 한다.")
    void resolveArgument_happyPath() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("methodWithModelAttribute", TestDto.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/")
                .addParameter("name", "hong")
                .addParameter("age", "30")
                .build();

        Object result = resolver.resolveArgument(parameter, request, null);

        assertNotNull(result);
        assertInstanceOf(TestDto.class, result);
        TestDto dto = (TestDto) result;
        assertEquals("hong", dto.getName());
        assertEquals(30, dto.getAge());
    }

    @Test
    @DisplayName("private 기본 생성자가 있는 DTO에 대해서도 접근 제어자를 해제하여 인스턴스를 생성하고 바인딩해야 한다.")
    void resolveArgument_privateConstructor() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("methodWithPrivateConstructor", TestPrivateConstructorDto.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/")
                .addParameter("name", "lee")
                .addParameter("age", "25")
                .build();

        Object result = resolver.resolveArgument(parameter, request, null);

        assertNotNull(result);
        assertInstanceOf(TestPrivateConstructorDto.class, result);
        TestPrivateConstructorDto dto = (TestPrivateConstructorDto) result;
        assertEquals("lee", dto.getName());
        assertEquals(25, dto.getAge());
    }

    @Test
    @DisplayName("매칭되는 쿼리스트링 파라미터가 아예 없거나 일부만 존재할 때, 기본값 유지 및 부분 바인딩이 정상 작동해야 한다.")
    void resolveArgument_partialOrNoParams() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("methodWithModelAttribute", TestDto.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        // name만 있고 age가 없음
        HttpServletRequest request1 = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/")
                .addParameter("name", "kim")
                .build();

        TestDto result1 = (TestDto) resolver.resolveArgument(parameter, request1, null);
        assertEquals("kim", result1.getName());
        assertEquals(0, result1.getAge());

        // 아무 파라미터도 없음
        HttpServletRequest request2 = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/")
                .build();

        TestDto result2 = (TestDto) resolver.resolveArgument(parameter, request2, null);
        assertNull(result2.getName());
        assertEquals(0, result2.getAge());
    }

    @Test
    @DisplayName("기본 생성자가 없는 경우 MethodArgumentTypeMismatchException이 발생해야 한다.")
    void resolveArgument_noDefaultConstructor_throwsException() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("methodWithNoDefaultConstructor", TestNoDefaultConstructorDto.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/")
                .build();

        assertThrows(MethodArgumentTypeMismatchException.class, () -> {
            resolver.resolveArgument(parameter, request, null);
        });
    }

    @Test
    @DisplayName("추상 클래스 타입인 경우 MethodArgumentTypeMismatchException이 발생해야 한다.")
    void resolveArgument_abstractClass_throwsException() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("methodWithAbstractClass", TestAbstractDto.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/")
                .build();

        assertThrows(MethodArgumentTypeMismatchException.class, () -> {
            resolver.resolveArgument(parameter, request, null);
        });
    }

    @Test
    @DisplayName("바인딩하려는 필드 타입을 지원하는 TypeConverter가 없는 경우 IllegalStateException이 발생해야 한다.")
    void resolveArgument_unsupportedFieldType_throwsException() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("methodWithUnsupportedField", TestUnsupportedFieldDto.class);
        MethodParameter parameter = MethodParameter.of(method, method.getParameters()[0], 0);

        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/")
                .addParameter("runnable", "dummy")
                .build();

        assertThrows(IllegalStateException.class, () -> {
            resolver.resolveArgument(parameter, request, null);
        });
    }
}
