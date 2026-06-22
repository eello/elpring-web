package eello.elpring.webtest.fixtures.duplicate;

import eello.elpring.di.annotation.Component;
import eello.elpring.web.bind.annotation.Controller;
import eello.elpring.web.bind.annotation.GetMapping;

@Controller
@Component
public class FakeDuplicateController1 {

    @GetMapping("/duplicate")
    public String duplicate() {
        return "dup1";
    }
}
