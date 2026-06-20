package eello.elpring.webtest.mapping;

import eello.elpring.di.context.AnnotationConfigApplicationContext;
import eello.elpring.web.DispatcherServlet;
import eello.elpring.webtest.fixtures.mapping.TomcatTestController;
import eello.elpring.webtest.fixtures.mapping.FakeMultiMappingController;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class DispatcherServletIntegrationTest {

    private static Tomcat tomcat;
    private static int port;
    private static AnnotationConfigApplicationContext context;

    @BeforeAll
    static void startTomcat() throws Exception {
        // 1. DI 컨테이너 기동
        context = new AnnotationConfigApplicationContext(
                "eello.elpring.webtest.fixtures.mapping",
                "eello.elpring.webtest.fixtures.requestparam",
                "eello.elpring.web"
        );
        context.refresh();

        // 2. 내장 톰캣 기동 설정
        tomcat = new Tomcat();
        tomcat.setPort(0); // 사용 가능한 임의의 포트 할당
        
        String baseDir = new File("build/tomcat").getAbsolutePath();
        tomcat.setBaseDir(baseDir);

        // Tomcat Context 생성 (존재하는 임시 webapp 경로 사용)
        File docBase = new File("build/tomcat/webapp");
        if (!docBase.exists()) {
            docBase.mkdirs();
        }
        Context ctx = tomcat.addContext("", docBase.getAbsolutePath());

        // DispatcherServlet 빈 조회 및 톰캣에 등록
        DispatcherServlet dispatcherServlet = context.getBean(DispatcherServlet.class);
        Wrapper wrapper = Tomcat.addServlet(ctx, "dispatcherServlet", dispatcherServlet);
        wrapper.setLoadOnStartup(1);
        ctx.addServletMappingDecoded("/*", "dispatcherServlet");

        // 톰캣 시작
        tomcat.start();
        port = tomcat.getConnector().getLocalPort();
        System.out.println("Embedded Tomcat started on port: " + port);
    }

    @AfterAll
    static void stopTomcat() throws Exception {
        if (tomcat != null) {
            tomcat.stop();
            tomcat.destroy();
        }
        // No close method exists on context
    }

    @Test
    void testEmbeddedTomcatDispatcherServletRouting() throws Exception {
        assertFalse(TomcatTestController.isCalled, "Flag should initially be false");

        // 1. HTTP Client를 사용해 실제 톰캣 서버로 요청 전송
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/test-tomcat"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 2. 검증
        assertEquals(200, response.statusCode(), "HTTP status code should be 200 OK");
        assertTrue(TomcatTestController.isCalled, "Controller method (testTomcat) should have been invoked");
    }

    @Test
    void testEmbeddedTomcatMultiMappingRouting() throws Exception {
        FakeMultiMappingController.reset();
        assertEquals(0, FakeMultiMappingController.callCount, "Initial callCount should be 0");

        HttpClient client = HttpClient.newHttpClient();
        
        String[] paths = {"/api/v1/users", "/api/v1/members", "/api/v2/users", "/api/v2/members"};
        String[] methods = {"GET", "POST"};

        for (String path : paths) {
            for (String method : methods) {
                HttpRequest.Builder builder = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + path));
                
                if ("GET".equals(method)) {
                    builder.GET();
                } else if ("POST".equals(method)) {
                    builder.POST(HttpRequest.BodyPublishers.noBody());
                }

                HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode(), "HTTP status code should be 200 OK for " + method + " " + path);
            }
        }

        assertEquals(8, FakeMultiMappingController.callCount, "FakeMultiMappingController should be called exactly 8 times");
    }

    @Test
    void testEmbeddedTomcatNotFoundRouting() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/no-exist"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "HTTP status code should be 404 Not Found for unregistered endpoint");
    }

    @Test
    void testEmbeddedTomcatPrimitiveResponse() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/test-tomcat/primitive"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("Content-Type").isPresent());
        assertTrue(response.headers().firstValue("Content-Type").get().startsWith("application/json"));
        assertEquals("100", response.body(), "Response body should be serialized primitive value");
    }

    @Test
    void testEmbeddedTomcatDtoResponse() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/test-tomcat/dto"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("Content-Type").isPresent());
        assertTrue(response.headers().firstValue("Content-Type").get().startsWith("application/json"));
        assertEquals("{\"name\":\"elpring\",\"age\":5}", response.body(), "Response body should be serialized DTO json");
    }

    @Test
    void testEmbeddedTomcatRequestParamBinding() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/test-requestparam?name=elpring&age=10"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("{\"name\":\"elpring\",\"age\":10}", response.body(), "Response body should be bound and serialized correctly");
    }

    @Test
    void testEmbeddedTomcatRequestParamCustomNameBinding() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/test-requestparam/custom-name?user_name=elpring&user_age=25"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("{\"name\":\"elpring\",\"age\":25}", response.body(), "Response body should be bound via custom names");
    }

    @Test
    void testEmbeddedTomcatRequestParamListAndArrayBinding() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/test-requestparam/list?tags=spring&tags=boot&scores=90&scores=100"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("{\"tags\":[\"spring\",\"boot\"],\"scores\":[90,100]}", response.body(), "Response body should bind list and array values");
    }
}
