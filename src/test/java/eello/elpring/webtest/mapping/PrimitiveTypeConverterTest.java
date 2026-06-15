package eello.elpring.webtest.mapping;

import eello.elpring.web.mapping.PrimitiveTypeConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PrimitiveTypeConverterTest {

    private final PrimitiveTypeConverter converter = new PrimitiveTypeConverter();

    @Test
    @DisplayName("프리미티브 및 래퍼 타입에 대해 supports가 true를 반환해야 한다.")
    void supports_primitive_types() {
        assertTrue(converter.supports(int.class));
        assertTrue(converter.supports(Integer.class));
        assertTrue(converter.supports(boolean.class));
        assertTrue(converter.supports(Double.class));
    }

    @Test
    @DisplayName("지원하지 않는 타입에 대해 supports가 false를 반환해야 한다.")
    void not_supports_other_types() {
        assertFalse(converter.supports(String.class));
        assertFalse(converter.supports(Object.class));
        assertFalse(converter.supports(int[].class));
    }

    @ParameterizedTest(name = "{index} => 입력값=''{1}'', 기대타입={0}")
    @CsvSource({
            "int, 10, 10",
            "java.lang.Integer, -5, -5",
            "double, 3.14, 3.14",
            "java.lang.Double, -0.5, -0.5",
            "boolean, true, true",
            "java.lang.Boolean, false, false"
    })
    @DisplayName("유효한 문자열 값을 프리미티브 타입으로 정상 변환해야 한다.")
    void convert_valid_string_to_primitive(Class<?> targetType, String inputValue, String expectedValue) {
        String[] rawValues = {inputValue};
        Object result = converter.convert(targetType, rawValues);

        if (targetType == int.class || targetType == Integer.class) {
            assertEquals(Integer.parseInt(expectedValue), result);
        } else if (targetType == double.class || targetType == Double.class) {
            assertEquals(Double.parseDouble(expectedValue), result);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            assertEquals(Boolean.parseBoolean(expectedValue), result);
        }
    }

    @Test
    @DisplayName("지원하지 않는 타입 변환을 시도할 때 IllegalStateException이 발생해야 한다.")
    void convert_unsupported_type_throws_exception() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> 
                converter.convert(String.class, new String[]{"hello"})
        );
        assertTrue(ex.getMessage().contains("Cannot convert primitive type java.lang.String to a primitive"));
    }

    @ParameterizedTest
    @ValueSource(classes = {int.class, Integer.class, long.class, Long.class})
    @DisplayName("숫자 타입으로 변환 불가능한 문자열 입력 시 NumberFormatException이 발생해야 한다.")
    void convert_invalid_number_throws_exception(Class<?> targetType) {
        assertThrows(NumberFormatException.class, () -> 
                converter.convert(targetType, new String[]{"abc"})
        );
    }
}
