package eello.elpring.webtest.fixtures.requestparam;

import eello.elpring.di.annotation.Component;
import eello.elpring.web.annotation.Controller;
import eello.elpring.web.annotation.GetMapping;
import eello.elpring.web.annotation.RequestParam;

@Controller
@Component
public class RequestParamTestController {

    public static class TestDto {
        private String name;
        private int age;

        public TestDto(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public int getAge() { return age; }
    }

    public static class ListDto {
        private java.util.List<String> tags;
        private int[] scores;

        public ListDto(java.util.List<String> tags, int[] scores) {
            this.tags = tags;
            this.scores = scores;
        }

        public java.util.List<String> getTags() { return tags; }
        public int[] getScores() { return scores; }
    }

    @GetMapping("/test-requestparam")
    public TestDto testRequestParam(@RequestParam("name") String name, @RequestParam("age") int age) {
        return new TestDto(name, age);
    }

    @GetMapping("/test-requestparam/custom-name")
    public TestDto testRequestParamCustomName(@RequestParam("user_name") String name, @RequestParam("user_age") int age) {
        return new TestDto(name, age);
    }

    @GetMapping("/test-requestparam/list")
    public ListDto testRequestParamList(@RequestParam("tags") java.util.List<String> tags, @RequestParam("scores") int[] scores) {
        return new ListDto(tags, scores);
    }
}
