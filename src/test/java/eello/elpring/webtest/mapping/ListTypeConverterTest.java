package eello.elpring.webtest.mapping;

import eello.elpring.web.mapping.CustomObjectTypeConverter;
import eello.elpring.web.mapping.ListTypeConverter;
import eello.elpring.web.mapping.PrimitiveTypeConverter;
import eello.elpring.web.mapping.ScalarTypeConverterManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListTypeConverterTest {

    private ListTypeConverter converter;

    @BeforeEach
    void setUp() {
        PrimitiveTypeConverter primitiveConverter = new PrimitiveTypeConverter();
        CustomObjectTypeConverter customObjectConverter = new CustomObjectTypeConverter();
        ScalarTypeConverterManager manager = new ScalarTypeConverterManager(
                List.of(primitiveConverter, customObjectConverter)
        );
        converter = new ListTypeConverter(manager);
    }

    @Test
    @DisplayName("List 계열 타입에 대해 supports가 true를 반환해야 한다.")
    void supports_list_types() {
        assertTrue(converter.supports(List.class));
        assertTrue(converter.supports(ArrayList.class));
    }

    @Test
    @DisplayName("List 계열이 아닌 타입에 대해 supports가 false를 반환해야 한다.")
    void not_supports_non_list_types() {
        assertFalse(converter.supports(int[].class));
        assertFalse(converter.supports(String[].class));
        assertFalse(converter.supports(String.class));
    }

    @Test
    @DisplayName("문자열 배열을 프리미티브(Integer) 리스트로 정상 변환해야 한다.")
    void convert_string_array_to_integer_list() {
        String[] input = {"1", "2", "3"};
        Object result = converter.convert(List.class, Integer.class, input);

        assertInstanceOf(List.class, result);
        List<?> list = (List<?>) result;
        assertEquals(3, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }

    @Test
    @DisplayName("단일 String 생성자를 가진 객체 리스트로 정상 변환해야 한다.")
    void convert_string_array_to_custom_object_list() {
        String[] input = {"apple", "banana"};
        Object result = converter.convert(List.class, CustomObj.class, input);

        assertInstanceOf(List.class, result);
        List<?> list = (List<?>) result;
        assertEquals(2, list.size());
        
        CustomObj first = (CustomObj) list.get(0);
        CustomObj second = (CustomObj) list.get(1);
        assertEquals("apple", first.value);
        assertEquals("banana", second.value);
    }

    @Test
    @DisplayName("컴포넌트 타입이 null인 경우 IllegalArgumentException이 발생해야 한다.")
    void convert_null_component_type_throws_exception() {
        assertThrows(IllegalArgumentException.class, () ->
                converter.convert(List.class, null, new String[]{"1"})
        );
    }

    // 테스트용 픽스처 클래스
    public static class CustomObj {
        public final String value;
        public CustomObj(String value) {
            this.value = value;
        }
    }
}
