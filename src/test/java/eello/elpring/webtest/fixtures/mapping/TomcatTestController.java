package eello.elpring.webtest.fixtures.mapping;

import eello.elpring.di.annotation.Component;
import eello.elpring.web.bind.annotation.Controller;
import eello.elpring.web.bind.annotation.GetMapping;
import eello.elpring.web.bind.annotation.PathVariable;

@Controller
@Component
public class TomcatTestController {

    public static boolean isCalled = false;

    public static class TestDto {
        private String name;
        private int age;

        public TestDto(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public int getAge() { return age; }
    }

    @GetMapping("/test-tomcat")
    public void testTomcat() {
        isCalled = true;
    }

    @GetMapping("/test-tomcat/primitive")
    public int testPrimitive() {
        return 100;
    }

    @GetMapping("/test-tomcat/dto")
    public TestDto testDto() {
        return new TestDto("elpring", 5);
    }

    @GetMapping("/test-tomcat/pathvariable/{id}/orders/{orderId}")
    public String testPathVariable(@PathVariable("id") int id,
                                   @PathVariable String orderId) {
        return "id:" + id + ",orderId:" + orderId;
    }
}
