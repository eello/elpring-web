package eello.elpring.webtest.fixtures.mapping;

import eello.elpring.di.annotation.Component;
import eello.elpring.web.bind.annotation.Controller;
import eello.elpring.web.bind.annotation.GetMapping;
import eello.elpring.web.bind.annotation.PathVariable;
import eello.elpring.web.bind.annotation.PostMapping;
import eello.elpring.web.bind.annotation.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@Component
public class TomcatTestController {

    public static boolean isCalled = false;

    public static class TestDto {
        private String name;
        private int age;

        public TestDto() {}

        public TestDto(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
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

    @PostMapping("/test-tomcat/requestbody")
    public TestDto testRequestBody(@RequestBody TestDto dto) {
        return dto;
    }

    @PostMapping("/test-tomcat/requestbody/list")
    public java.util.List<TestDto> testRequestBodyList(@RequestBody java.util.List<TestDto> list) {
        return list;
    }

    @GetMapping("/test-tomcat/servlet-api")
    public String testServletApi(HttpServletRequest request, HttpServletResponse response) {
        String reqHeader = request.getHeader("X-Custom-Request");
        response.setHeader("X-Custom-Response", reqHeader + "-Received");
        return "success";
    }
}
