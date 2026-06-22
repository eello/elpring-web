package eello.elpring.web.bind.convert;

import java.util.List;

public interface TypeConverterManager<T extends TypeConverter> {

    boolean supports(Class<?> targetType);
    TypeConverter getTypeConverter(Class<?> targetType);
    default void addTypeConverter(T converter) {

    }
}
