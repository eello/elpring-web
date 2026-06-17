package eello.elpring.webtest.mapping;

import eello.elpring.web.exception.MethodArgumentTypeMismatchException;
import eello.elpring.web.mapping.CustomObjectTypeConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CustomObjectTypeConverterTest {

    private final CustomObjectTypeConverter converter = new CustomObjectTypeConverter();

    @Test
    @DisplayName("일반 사용자 정의 단일 객체 타입에 대해 supports가 true를 반환해야 한다.")
    void supports_custom_object_types() {
        assertTrue(converter.supports(CustomObj.class));
        assertTrue(converter.supports(FactoryObj.class));
    }

    @Test
    @DisplayName("배열, 컬렉션, 프리미티브, 인터페이스, 맵 타입에 대해 supports가 false를 반환해야 한다.")
    void not_supports_special_types() {
        assertFalse(converter.supports(int.class));
        assertFalse(converter.supports(int[].class));
        assertFalse(converter.supports(Collection.class));
        assertFalse(converter.supports(List.class));
        assertFalse(converter.supports(Map.class));
        assertFalse(converter.supports(Runnable.class)); // interface
    }

    @Test
    @DisplayName("단일 String 생성자를 가진 객체로 정상 변환해야 한다.")
    void convert_with_constructor() {
        Object result = converter.convert(CustomObj.class, new String[]{"hello"});

        assertInstanceOf(CustomObj.class, result);
        assertEquals("hello", ((CustomObj) result).value);
    }

    @Test
    @DisplayName("단일 String 매개변수의 정적 팩토리 메서드를 가진 객체로 정상 변환해야 한다.")
    void convert_with_factory_method() {
        Object result = converter.convert(FactoryObj.class, new String[]{"42"});

        assertInstanceOf(FactoryObj.class, result);
        assertEquals(42, ((FactoryObj) result).number);
    }

    @Test
    @DisplayName("지원하지 않는 타입을 변환하려 할 때 MethodArgumentTypeMismatchException이 발생해야 한다.")
    void convert_unsupported_type_throws_exception() {
        assertThrows(MethodArgumentTypeMismatchException.class, () -> 
                converter.convert(int.class, new String[]{"10"})
        );
    }

    @Test
    @DisplayName("변환 생성자나 팩토리 메서드가 없는 경우 MethodArgumentTypeMismatchException이 발생해야 한다.")
    void convert_no_factory_throws_exception() {
        assertThrows(MethodArgumentTypeMismatchException.class, () -> 
                converter.convert(NoFactoryObj.class, new String[]{"fail"})
        );
    }

    @Test
    @DisplayName("Enum 타입에 대해 supports가 true를 반환하고 정상 변환되어야 한다.")
    void convert_enum_type() {
        assertTrue(converter.supports(TestEnum.class));
        
        Object result = converter.convert(TestEnum.class, new String[]{"GREEN"});
        
        assertInstanceOf(TestEnum.class, result);
        assertEquals(TestEnum.GREEN, result);
    }

    @Test
    @DisplayName("래퍼 클래스(Integer)에 대해 supports가 true를 반환하고 정상 변환되어야 한다.")
    void convert_wrapper_class() {
        assertTrue(converter.supports(Integer.class));
        
        Object result = converter.convert(Integer.class, new String[]{"123"});
        
        assertInstanceOf(Integer.class, result);
        assertEquals(123, result);
    }

    // 테스트용 픽스처 클래스들
    public enum TestEnum {
        RED, GREEN, BLUE
    }

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
