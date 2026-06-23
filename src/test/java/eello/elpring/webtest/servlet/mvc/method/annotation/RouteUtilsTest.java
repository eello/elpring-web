package eello.elpring.webtest.servlet.mvc.method.annotation;

import eello.elpring.web.servlet.mvc.method.annotation.RouteUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class RouteUtilsTest {

    @Test
    @DisplayName("경로에 중괄호 형식의 PathVariable이 존재하는지 정상적으로 검사해야 한다.")
    void testHasPathVariable() {
        assertTrue(RouteUtils.hasPathVariable("/users/{id}"));
        assertTrue(RouteUtils.hasPathVariable("/categories/{category}/products/{productId}"));
        assertFalse(RouteUtils.hasPathVariable("/users/profile"));
        assertFalse(RouteUtils.hasPathVariable("/"));
        assertFalse(RouteUtils.hasPathVariable(null));
        assertFalse(RouteUtils.hasPathVariable(""));
    }

    @Test
    @DisplayName("경로에서 PathVariable 변수명들을 정상적으로 추출해야 한다.")
    void testExtractVariableNames() {
        List<String> singleVar = RouteUtils.extractVariableNames("/users/{id}");
        assertEquals(1, singleVar.size());
        assertEquals("id", singleVar.get(0));

        List<String> multiVars = RouteUtils.extractVariableNames("/categories/{category}/products/{productId}");
        assertEquals(2, multiVars.size());
        assertEquals("category", multiVars.get(0));
        assertEquals("productId", multiVars.get(1));

        List<String> noVar = RouteUtils.extractVariableNames("/users/profile");
        assertTrue(noVar.isEmpty());
    }

    @Test
    @DisplayName("PathVariable 중괄호가 포함된 경로가 정규식 패턴으로 알맞게 변환되어야 한다.")
    void testConvertToRegexPattern() {
        Pattern pattern1 = RouteUtils.convertToRegexPattern("/users/{id}");
        assertNotNull(pattern1);
        assertTrue(pattern1.matcher("/users/123").matches());
        assertTrue(pattern1.matcher("/users/abc").matches());
        assertFalse(pattern1.matcher("/users/123/profile").matches());
        assertFalse(pattern1.matcher("/users/").matches());

        Pattern pattern2 = RouteUtils.convertToRegexPattern("/categories/{category}/products/{productId}");
        assertNotNull(pattern2);
        assertTrue(pattern2.matcher("/categories/books/products/456").matches());
        assertFalse(pattern2.matcher("/categories/books/products").matches());
    }
}
