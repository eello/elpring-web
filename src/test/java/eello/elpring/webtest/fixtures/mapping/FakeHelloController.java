package eello.elpring.webtest.fixtures.mapping;

import eello.elpring.di.annotation.Component;
import eello.elpring.web.bind.annotation.Controller;
import eello.elpring.web.bind.annotation.GetMapping;

@Controller
@Component
public class FakeHelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
