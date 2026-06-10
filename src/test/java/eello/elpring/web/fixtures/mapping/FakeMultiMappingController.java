package eello.elpring.web.fixtures.mapping;

import eello.elpring.di.annotation.Component;
import eello.elpring.web.annotation.Controller;
import eello.elpring.web.annotation.RequestMapping;
import eello.elpring.web.annotation.RequestMethod;

@Controller
@Component
@RequestMapping({"/api/v1", "/api/v2"})
public class FakeMultiMappingController {

    @RequestMapping(value = {"/users", "/members"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String getOrPostUsers() {
        return "multiMapping";
    }
}
