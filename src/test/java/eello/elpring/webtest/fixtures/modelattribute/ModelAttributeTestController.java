package eello.elpring.webtest.fixtures.modelattribute;

import eello.elpring.di.annotation.Component;
import eello.elpring.web.bind.annotation.Controller;
import eello.elpring.web.bind.annotation.GetMapping;
import eello.elpring.web.bind.annotation.ModelAttribute;

@Controller
@Component
public class ModelAttributeTestController {

    public static class UserDto {
        private String name;
        private int age;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }

    public static class TestUnsupportedFieldDto {
        private Runnable runnable;
        public Runnable getRunnable() { return runnable; }
        public void setRunnable(Runnable runnable) { this.runnable = runnable; }
    }

    @GetMapping("/test-modelattribute")
    public UserDto testModelAttribute(@ModelAttribute UserDto userDto) {
        return userDto;
    }

    @GetMapping("/test-modelattribute/no-annotation")
    public UserDto testModelAttributeNoAnnotation(UserDto userDto) {
        return userDto;
    }

    @GetMapping("/test-modelattribute/unsupported")
    public TestUnsupportedFieldDto testModelAttributeUnsupported(TestUnsupportedFieldDto dto) {
        return dto;
    }
}
