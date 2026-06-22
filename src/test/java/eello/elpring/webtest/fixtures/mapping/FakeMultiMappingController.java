package eello.elpring.webtest.fixtures.mapping;

import eello.elpring.di.annotation.Component;
import eello.elpring.web.bind.annotation.Controller;
import eello.elpring.web.bind.annotation.RequestMapping;
import eello.elpring.web.bind.annotation.RequestMethod;

@Controller
@Component
@RequestMapping({"/api/v1", "/api/v2"})
public class FakeMultiMappingController {

    public static int callCount = 0;

    public static void reset() {
        callCount = 0;
    }

    @RequestMapping(value = {"/users", "/members"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String getOrPostUsers() {
        callCount++;
        return "multiMapping";
    }
}
