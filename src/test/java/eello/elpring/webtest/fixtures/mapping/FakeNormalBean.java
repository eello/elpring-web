package eello.elpring.webtest.fixtures.mapping;

import eello.elpring.di.annotation.Component;
import eello.elpring.web.bind.annotation.GetMapping;

@Component
public class FakeNormalBean {

    @GetMapping("/ignored")
    public String ignored() {
        return "ignored";
    }
}
