package eello.elpring.webtest.servlet.mvc.method.annotation;
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
import eello.elpring.web.method.HandlerMethod;
import eello.elpring.web.method.annotation.RequestParamMethodArgumentResolver;
import eello.elpring.web.method.support.HandlerMethodArgumentResolverComposite;
import eello.elpring.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import eello.elpring.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class RequestMappingHandlerAdapterTest {

    private RequestMappingHandlerAdapter adapter;
    private TestController controller;
    private HttpServletResponse response; // 모킹할 필요가 없는 빈 proxy 혹은 null 가능 (여기서는 Proxy로 임시 생성 가능하지만 null도 무방할 경우 null 사용)

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
        
        TypeConversionService conversionService = new TypeConversionService(managers);
        RequestParamMethodArgumentResolver resolver = new RequestParamMethodArgumentResolver(conversionService);
        
        HandlerMethodArgumentResolverComposite resolverComposite = new HandlerMethodArgumentResolverComposite();
        resolverComposite.addArgumentResolver(resolver);
        
        tools.jackson.databind.ObjectMapper objectMapper = new tools.jackson.databind.ObjectMapper();
        adapter = new RequestMappingHandlerAdapter(objectMapper, resolverComposite);
        controller = new TestController();
        response = null; // HandlerAdapter.handle에 response는 현재 전달되지만 사용은 되지 않음
    }

    public static class TestController {
        public String simpleParam(@RequestParam("name") String name) {
            return name;
        }

        public String mixParam(@RequestParam("name") String name, HttpServletRequest skip, @RequestParam("age") int age) {
            return name + ":" + skip + ":" + age;
        }

        public String customParam(@RequestParam("user_name") String name) {
            return name;
        }
    }

    @Test
    @DisplayName("단일 @RequestParam이 지정된 경우, 요청 파라미터가 정상적으로 바인딩되어야 한다.")
    void testHandle_SimpleParam() throws Exception {
        Method method = TestController.class.getMethod("simpleParam", String.class);
        HandlerMethod handlerMethod = new HandlerMethod(method, "testController", controller, TestController.class);

        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/simple")
                .addParameter("name", "elpring")
                .build();

        String result = adapter.handle(request, response, handlerMethod);

        // ObjectMapper를 통해 직렬화된 반환값 검증 (String의 경우 "elpring" json 형식으로 직렬화)
        assertEquals("\"elpring\"", result);
    }

    @Test
    @DisplayName("지원하지 않는 일반 파라미터가 섞여 있을 때, 예외가 발생해야 한다.")
    void testHandle_MixParam_ThrowsException() throws Exception {
        Method method = TestController.class.getMethod("mixParam", String.class, HttpServletRequest.class, int.class);
        HandlerMethod handlerMethod = new HandlerMethod(method, "testController", controller, TestController.class);

        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/mix")
                .addParameter("name", "elpring")
                .addParameter("age", "5")
                .build();

        // 지원하는 아규먼트 리졸버가 없는 파라미터가 섞여 있을 경우, IllegalStateException이 던져져야 함.
        assertThrows(IllegalStateException.class, () -> {
            adapter.handle(request, response, handlerMethod);
        });
    }

    @Test
    @DisplayName("@RequestParam의 value 속성으로 커스텀 이름이 지정된 경우, 변수명 대신 해당 이름으로 바인딩되어야 한다.")
    void testHandle_CustomNameParam() throws Exception {
        Method method = TestController.class.getMethod("customParam", String.class);
        HandlerMethod handlerMethod = new HandlerMethod(method, "testController", controller, TestController.class);

        HttpServletRequest request = FakeHttpServletRequest.builder()
                .method("GET")
                .uri("/custom")
                .addParameter("user_name", "elpring") // 변수명인 name이 아니라 user_name으로 전달됨
                .build();

        String result = adapter.handle(request, response, handlerMethod);

        // 정상 동작할 경우, user_name의 값인 elpring이 바인딩되어야 함.
        assertEquals("\"elpring\"", result);
    }
}
