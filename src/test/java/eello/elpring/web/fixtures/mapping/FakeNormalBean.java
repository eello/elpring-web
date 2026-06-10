package eello.elpring.web.fixtures.mapping;

import eello.elpring.di.annotation.Component;
import eello.elpring.web.annotation.GetMapping;

@Component
public class FakeNormalBean {

    @GetMapping("/ignored")
    public String ignored() {
        return "ignored";
    }
}
