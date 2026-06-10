package eello.elpring.web.fixtures.mapping;

import eello.elpring.di.annotation.Component;
import eello.elpring.web.annotation.Controller;
import eello.elpring.web.annotation.GetMapping;

@Controller
@Component
public class FakeHelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
