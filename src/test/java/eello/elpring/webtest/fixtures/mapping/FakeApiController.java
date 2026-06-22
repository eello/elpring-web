package eello.elpring.webtest.fixtures.mapping;

import eello.elpring.di.annotation.Component;
import eello.elpring.web.bind.annotation.Controller;
import eello.elpring.web.bind.annotation.GetMapping;
import eello.elpring.web.bind.annotation.PostMapping;
import eello.elpring.web.bind.annotation.RequestMapping;

@Controller
@Component
@RequestMapping("/api")
public class FakeApiController {

    @PostMapping("/users")
    public String createUser() {
        return "userCreated";
    }

    @GetMapping("//items/")
    public String getItems() {
        return "items";
    }
}
