package eello.elpring.web.mapping;

public interface TypeConverter {

    boolean supports(Class<?> targetType);

    /*
        targetType이 객체 타입인 경우 단일 String의 파라미터를 갖는 생성자 혹은 valueOf 함수가 없는 경우
        throw MethodArgumentTypeMismatchException
     */
    Object convert(Class<?> targetType, String[] rawValues);
}
