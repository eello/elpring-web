package eello.elpring.web.mapping;

import eello.elpring.di.annotation.Bean;
import eello.elpring.di.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class TypeConverterConfig {

    @Bean
    public ScalarTypeConverterManager scalarTypeConverterManager() {
        ScalarTypeConverterManager scalarTypeConverterManager = new ScalarTypeConverterManager();
        scalarTypeConverterManager.addTypeConverter(new PrimitiveTypeConverter());
        scalarTypeConverterManager.addTypeConverter(new CustomObjectTypeConverter());
        return scalarTypeConverterManager;
    }

    @Bean
    public CollectionTypeConverterManager collectionTypeConverterManager(ScalarTypeConverterManager scalarTypeConverterManager) {
        CollectionTypeConverterManager collectionTypeConverterManager = new CollectionTypeConverterManager();
        collectionTypeConverterManager.addTypeConverter(new ArrayTypeConverter(scalarTypeConverterManager));
        collectionTypeConverterManager.addTypeConverter(new ListTypeConverter(scalarTypeConverterManager));
        collectionTypeConverterManager.addTypeConverter(new SetTypeConverter(scalarTypeConverterManager));
        return collectionTypeConverterManager;
    }

    @Bean
    public RequestParamConversionService requestParamConversionService(
            ScalarTypeConverterManager scalarTypeConverterManager,
            CollectionTypeConverterManager collectionTypeConverterManager) {
        List<TypeConverterManager<? extends TypeConverter>> managers = new ArrayList<>();
        managers.add(scalarTypeConverterManager);
        managers.add(collectionTypeConverterManager);
        return new RequestParamConversionService(managers);
    }
}
