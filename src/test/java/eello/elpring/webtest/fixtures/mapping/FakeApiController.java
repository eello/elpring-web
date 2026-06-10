package eello.elpring.webtest.fixtures.mapping;

import eello.elpring.di.annotation.Component;
import eello.elpring.web.annotation.Controller;
import eello.elpring.web.annotation.GetMapping;
import eello.elpring.web.annotation.PostMapping;
import eello.elpring.web.annotation.RequestMapping;

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
