package eello.elpring.webtest.mapping;

import eello.elpring.web.exception.MethodArgumentTypeMismatchException;
import eello.elpring.web.mapping.ArrayTypeConverter;
import eello.elpring.web.mapping.CustomObjectTypeConverter;
import eello.elpring.web.mapping.PrimitiveTypeConverter;
import eello.elpring.web.mapping.ScalarTypeConverterManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTypeConverterTest {

    private ArrayTypeConverter converter;

    @BeforeEach
    void setUp() {
        PrimitiveTypeConverter primitiveConverter = new PrimitiveTypeConverter();
        CustomObjectTypeConverter customObjectConverter = new CustomObjectTypeConverter();
        ScalarTypeConverterManager manager = new ScalarTypeConverterManager(
                List.of(primitiveConverter, customObjectConverter)
        );
        converter = new ArrayTypeConverter(manager);
    }

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
        Object result = converter.convert(int[].class, int.class, input);

        assertInstanceOf(int[].class, result);
        int[] intArray = (int[]) result;
        assertArrayEquals(new int[]{1, 2, 3}, intArray);
    }

    @Test
    @DisplayName("단일 String 생성자를 가진 객체 배열로 정상 변환해야 한다.")
    void convert_string_array_to_custom_object_array_with_constructor() {
        String[] input = {"apple", "banana"};
        Object result = converter.convert(CustomObj[].class, CustomObj.class, input);

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
        Object result = converter.convert(FactoryObj[].class, FactoryObj.class, input);

        assertInstanceOf(FactoryObj[].class, result);
        FactoryObj[] factoryArray = (FactoryObj[]) result;
        assertEquals(2, factoryArray.length);
        assertEquals(100, factoryArray[0].number);
        assertEquals(200, factoryArray[1].number);
    }

    @Test
    @DisplayName("지원하지 않는 타입 변환을 시도할 때 MethodArgumentTypeMismatchException이 발생해야 한다.")
    void convert_unsupported_type_throws_exception() {
        assertThrows(MethodArgumentTypeMismatchException.class, () -> 
                converter.convert(String.class, NoFactoryObj.class, new String[]{"hello"}) // supports 실패 혹은 converter 획득 실패
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
        public NoFactoryObj(int value) {
        }
    }
}
