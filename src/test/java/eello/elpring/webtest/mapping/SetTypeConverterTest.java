package eello.elpring.webtest.mapping;

import eello.elpring.web.mapping.CustomObjectTypeConverter;
import eello.elpring.web.mapping.PrimitiveTypeConverter;
import eello.elpring.web.mapping.ScalarTypeConverterManager;
import eello.elpring.web.mapping.SetTypeConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SetTypeConverterTest {

    private SetTypeConverter converter;

    @BeforeEach
    void setUp() {
        PrimitiveTypeConverter primitiveConverter = new PrimitiveTypeConverter();
        CustomObjectTypeConverter customObjectConverter = new CustomObjectTypeConverter();
        ScalarTypeConverterManager manager = new ScalarTypeConverterManager();
        manager.addTypeConverter(primitiveConverter);
        manager.addTypeConverter(customObjectConverter);
        converter = new SetTypeConverter(manager);
    }

    @Test
    @DisplayName("Set 계열 타입에 대해 supports가 true를 반환해야 한다.")
    void supports_set_types() {
        assertTrue(converter.supports(Set.class));
        assertTrue(converter.supports(HashSet.class));
    }

    @Test
    @DisplayName("Set 계열이 아닌 타입에 대해 supports가 false를 반환해야 한다.")
    void not_supports_non_set_types() {
        assertFalse(converter.supports(int[].class));
        assertFalse(converter.supports(List.class));
        assertFalse(converter.supports(String.class));
    }

    @Test
    @DisplayName("문자열 배열을 프리미티브(Integer) 세트로 정상 변환하고 중복을 제거해야 한다.")
    void convert_string_array_to_integer_set() {
        String[] input = {"1", "2", "2", "3"};
        Object result = converter.convert(Set.class, Integer.class, input);

        assertInstanceOf(Set.class, result);
        Set<?> set = (Set<?>) result;
        assertEquals(3, set.size());
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
    }

    @Test
    @DisplayName("단일 String 생성자를 가진 객체 세트로 정상 변환해야 한다.")
    void convert_string_array_to_custom_object_set() {
        String[] input = {"apple", "banana", "apple"};
        Object result = converter.convert(Set.class, CustomObj.class, input);

        assertInstanceOf(Set.class, result);
        Set<?> set = (Set<?>) result;
        // CustomObj가 equals/hashCode를 오버라이드하지 않았으므로 문자열 내용은 중복되더라도 객체 인스턴스는 3개 생성되어 들어갑니다.
        assertEquals(3, set.size()); 
    }

    @Test
    @DisplayName("컴포넌트 타입이 null인 경우 IllegalArgumentException이 발생해야 한다.")
    void convert_null_component_type_throws_exception() {
        assertThrows(IllegalArgumentException.class, () ->
                converter.convert(Set.class, null, new String[]{"1"})
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
