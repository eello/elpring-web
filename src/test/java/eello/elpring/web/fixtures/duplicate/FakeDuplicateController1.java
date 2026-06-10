package eello.elpring.web.fixtures.duplicate;

import eello.elpring.di.annotation.Component;
import eello.elpring.web.annotation.Controller;
import eello.elpring.web.annotation.GetMapping;

@Controller
@Component
public class FakeDuplicateController1 {

    @GetMapping("/duplicate")
    public String duplicate() {
        return "dup1";
    }
}
