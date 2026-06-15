package eello.elpring.webtest.mapping;

import eello.elpring.web.exception.MethodArgumentTypeMismatchException;
import eello.elpring.web.mapping.ArrayTypeConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTypeConverterTest {

    private final ArrayTypeConverter converter = new ArrayTypeConverter();

    @Test
    @DisplayName("배열 타입에 대해 supports가 true를 반환해야 한다.")
    void supports_array_types() {
        assertTrue(converter.supports(int[].class));
        assertTrue(converter.supports(String[].class));
        assertTrue(converter.supports(CustomObj[].class));
    }

    @Test
    @DisplayName("배열이 아닌 타입에 대해 supports가 false를 반환해야 한다.")
    void not_supports_non_array_types() {
        assertFalse(converter.supports(int.class));
        assertFalse(converter.supports(String.class));
        assertFalse(converter.supports(CustomObj.class));
    }

    @Test
    @DisplayName("문자열 배열을 프리미티브 타입 배열로 정상 변환해야 한다.")
    void convert_string_array_to_primitive_array() {
        String[] input = {"1", "2", "3"};
        Object result = converter.convert(int[].class, input);

        assertInstanceOf(int[].class, result);
        int[] intArray = (int[]) result;
        assertArrayEquals(new int[]{1, 2, 3}, intArray);
    }

    @Test
    @DisplayName("단일 String 생성자를 가진 객체 배열로 정상 변환해야 한다.")
    void convert_string_array_to_custom_object_array_with_constructor() {
        String[] input = {"apple", "banana"};
        Object result = converter.convert(CustomObj[].class, input);

        assertInstanceOf(CustomObj[].class, result);
        CustomObj[] customArray = (CustomObj[]) result;
        assertEquals(2, customArray.length);
        assertEquals("apple", customArray[0].value);
        assertEquals("banana", customArray[1].value);
    }

    @Test
    @DisplayName("단일 String 정적 팩토리 메서드를 가진 객체 배열로 정상 변환해야 한다.")
    void convert_string_array_to_custom_object_array_with_factory_method() {
        String[] input = {"100", "200"};
        Object result = converter.convert(FactoryObj[].class, input);

        assertInstanceOf(FactoryObj[].class, result);
        FactoryObj[] factoryArray = (FactoryObj[]) result;
        assertEquals(2, factoryArray.length);
        assertEquals(100, factoryArray[0].number);
        assertEquals(200, factoryArray[1].number);
    }

    @Test
    @DisplayName("지원하지 않는 타입 변환을 시도할 때 IllegalStateException이 발생해야 한다.")
    void convert_unsupported_type_throws_exception() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> 
                converter.convert(String.class, new String[]{"hello"})
        );
        assertTrue(ex.getMessage().contains("Cannot convert array of type"));
    }

    @Test
    @DisplayName("String 생성자나 팩토리 메서드가 없는 객체 배열로 변환 시 MethodArgumentTypeMismatchException이 발생해야 한다.")
    void convert_no_factory_method_throws_exception() {
        String[] input = {"test"};
        assertThrows(MethodArgumentTypeMismatchException.class, () -> 
                converter.convert(NoFactoryObj[].class, input)
        );
    }

    // 테스트용 픽스처 클래스들
    public static class CustomObj {
        public final String value;
        public CustomObj(String value) {
            this.value = value;
        }
    }

    public static class FactoryObj {
        public final int number;
        private FactoryObj(int number) {
            this.number = number;
        }
        public static FactoryObj of(String value) {
            return new FactoryObj(Integer.parseInt(value));
        }
    }

    public static class NoFactoryObj {
        // 단일 String 매개변수 생성자 없음
        public NoFactoryObj(int value) {
        }
    }
}
