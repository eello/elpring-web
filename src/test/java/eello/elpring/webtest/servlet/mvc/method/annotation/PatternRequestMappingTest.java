package eello.elpring.webtest.servlet.mvc.method.annotation;

import eello.elpring.web.bind.annotation.RequestMethod;
import eello.elpring.web.servlet.mvc.method.annotation.PatternRequestMapping;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PatternRequestMappingTest {

    @Test
    @DisplayName("HTTP 메서드와 정규식 패턴이 매치되는지 정상적으로 확인해야 한다.")
    void testIsMatch() {
        PatternRequestMapping route = PatternRequestMapping.of(RequestMethod.GET, "/users/{id}");

        // Method & URL match
        assertTrue(route.isMatch("/users/123", RequestMethod.GET));
        assertTrue(route.isMatch("/users/abc", RequestMethod.GET));

        // Method mismatch
        assertFalse(route.isMatch("/users/123", RequestMethod.POST));

        // URL mismatch
        assertFalse(route.isMatch("/users/123/profile", RequestMethod.GET));
        assertFalse(route.isMatch("/orders/123", RequestMethod.GET));
    }

    @Test
    @DisplayName("매치된 요청 URL로부터 경로 변수 값을 올바르게 추출해야 한다.")
    void testExtractPathVariables() {
        PatternRequestMapping route = PatternRequestMapping.of(
                RequestMethod.GET, 
                "/categories/{category}/products/{productId}"
        );

        Map<String, String> variables = route.extractPathVariables("/categories/books/products/999");
        
        assertNotNull(variables);
        assertEquals(2, variables.size());
        assertEquals("books", variables.get("category"));
        assertEquals("999", variables.get("productId"));

        // Match fail인 경우 빈 맵이 나오거나 혹은 매칭이 안되는 경우
        Map<String, String> noMatchVariables = route.extractPathVariables("/categories/books/products");
        assertTrue(noMatchVariables.isEmpty());
    }
}
