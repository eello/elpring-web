[![](https://jitpack.io/v/eello/elpring-web.svg)](https://jitpack.io/#eello/elpring-web)

# elpring-web (경량 Web MVC 프레임워크)

`elpring-web`은 Spring MVC의 핵심 기술인 **DispatcherServlet**과 **Argument Resolver**의 요청 라우팅 및 데이터 파싱 동작 원리를 깊이 있게 학습하고 이해하기 위해 순수 Java 및 내장 톰캣(Tomcat) 환경을 기반으로 구현한 경량 Web MVC 프레임워크 라이브러리입니다.

본 프로젝트는 의존성 주입(DI) 컨테이너인 **[elpring-di](file:///Users/jongseong/01.%20Projects/elpring-framework/elpring-di)**와 유기적으로 연동하여 동작하며, 컴포넌트 스캔 및 어노테이션 기반 MVC 패턴을 완벽히 지원합니다.

---

## 🛠 기술 스택

- **Core**: Java 21 (JDK 21)
- **Servlet Container (WAS)**: Embedded Tomcat 10.1 (Jakarta Servlet API 6.x)
- **JSON Serialization**: Jackson Databind 3.1
- **DI 연동**: `elpring-di` (자체 DI 프레임워크 라이브러리)
- **빌드 도구**: Gradle
- **테스트 프레임워크**: JUnit 5

---

## ✨ 핵심 기능

1. **DispatcherServlet 기반 프론트 컨트롤러 패턴**:
   - 모든 HTTP 요청을 단일 진입점인 `DispatcherServlet`에서 일괄 수신하여 적절한 컨트롤러 핸들러로 중개 및 배포합니다.
2. **어노테이션 기반 URL 매핑 & 라우팅**:
   - `@Controller`, `@RequestMapping`, `@GetMapping`, `@PostMapping` 등 어노테이션을 스캔 및 매핑합니다.
   - URI 템플릿 패턴 매치 엔진을 탑재하여 `/api/todos/{id}`와 같은 동적 세그먼트 매핑 및 정규식 바인딩을 지원합니다.
3. **아규먼트 리졸버 아키텍처 (Argument Resolvers)**:
   - 컨트롤러 핸들러 메서드의 파라미터 타입을 실행 시점에 동적으로 분석하여 필요한 인스턴스를 자동으로 주입합니다.
   - **`@RequestParam`**: 쿼리 파라미터 바인딩 (원시타입 변환, List/Array 맵핑, 어노테이션 생략 시 Fallback 처리 지원).
   - **`@PathVariable`**: URI 템플릿에서 경로 변수를 추출 및 주입.
   - **`@ModelAttribute`**: 쿼리 스트링 값을 DTO(Data Transfer Object) 객체 필드에 리플렉션 바인딩 (어노테이션 생략 시 Fallback 처리 지원).
   - **`@RequestBody`**: HTTP Request Body 내 JSON 문자열을 Java 객체 및 중첩 제네릭 컬렉션(`List<DTO>`)으로 역직렬화 및 바인딩.
   - **서블릿 원시 API 주입**: `HttpServletRequest`, `HttpServletResponse` 객체 직접 주입 지원.
4. **확장 가능한 메시지 컨버터 시스템 (HttpMessageConverter)**:
   - 요청/응답 콘텐츠 타입(Content-Type / Accept 헤더)에 따라 본문 직렬화 및 역직렬화를 담당합니다.
   - `StringHttpMessageConverter`: 문자열(`text/plain`) 바디 교환 처리.
   - `JacksonJsonHttpMessageConverter`: JSON 객체 및 컬렉션(`application/json`) 처리.

---

## 📂 프로젝트 패키지 구조

```
src/main/java/
└── eello/
    └── elpring/
        └── web/
            ├── bind/
            │   ├── annotation/
            │   │   ├── Controller.java             # 컨트롤러 마킹 어노테이션
            │   │   ├── RequestMapping.java         # HTTP 경로 매핑 공통 정의
            │   │   ├── GetMapping.java             # HTTP GET 단축 매핑
            │   │   ├── PostMapping.java            # HTTP POST 단축 매핑
            │   │   ├── RequestParam.java           # 쿼리 파라미터 매핑 마커
            │   │   ├── PathVariable.java           # 경로 변수 매핑 마커
            │   │   ├── ModelAttribute.java         # 쿼리 파라미터 DTO 매핑 마커
            │   │   └── RequestBody.java            # HTTP 요청 본문 역직렬화 마커
            │   └── support/
            │       └── TypeConversionService.java  # 문자열 값을 Target 타입으로 변환하는 변환 허브
            ├── config/
            │   └── ArgumentResolverConfig.java     # 기본 아규먼트 리졸버 및 메시지 컨버터 등록 설정
            ├── http/
            │   ├── MediaType.java                  # Content-Type 정보 래핑 및 비교 유틸
            │   └── converter/
            │       ├── HttpMessageConverter.java   # 바디 변환 처리를 위한 추상 규약
            │       ├── GenericHttpMessageConverter.java # 중첩 제네릭(List 등) 변환을 지원하는 확장 명세
            │       ├── StringHttpMessageConverter.java # String <-> text/plain 변환 구현체
            │       └── JacksonJsonHttpMessageConverter.java # Object/List <-> application/json 변환 구현체
            ├── method/
            │   ├── HandlerMethod.java              # 매핑 대상 컨트롤러와 자바 메서드 메타데이터 홀더
            │   ├── MethodParameter.java            # 실행할 메서드 인자의 타입 및 어노테이션 상태 추상화
            │   └── annotation/
            │       ├── ModelAttributeMethodArgumentResolver.java # @ModelAttribute 바인더
            │       ├── PathVariableMethodArgumentResolver.java   # @PathVariable 바인더
            │       ├── RequestBodyMethodArgumentResolver.java    # @RequestBody JSON/Text 바인더
            │       ├── RequestParamMethodArgumentResolver.java   # @RequestParam 파라미터 바인더
            │       ├── ServletRequestMethodArgumentResolver.java # HttpServletRequest 주입 처리
            │       └── ServletResponseMethodArgumentResolver.java # HttpServletResponse 주입 처리
            ├── servlet/
            │   ├── DispatcherServlet.java          # HTTP 요청 진입 제어 및 중개 프론트 컨트롤러
            │   ├── HandlerMapping.java             # 요청 URL에 부합하는 실행 체인 검색 명세
            │   ├── HandlerAdapter.java             # 대상 컨트롤러의 실제 실행을 담당하는 어댑터 명세
            │   └── mvc/
            │       ├── RequestKey.java             # 정적 경로 및 HTTP 메서드 키 정의
            │       └── method/
            │           └── annotation/
            │               ├── RequestMappingHandlerMapping.java # 컨트롤러 스캔 및 라우팅 맵핑 핵심 레지스트리
            │               ├── RequestMappingHandlerAdapter.java # 리졸버 연동 및 Jackson 반환값 직렬화 실행 엔진
            │               ├── PatternRequestMapping.java       # PathVariable이 포함된 동적 경로 매칭 래퍼
            │               └── RouteUtils.java                  # 경로 분석 및 정규식 치환 보조 헬퍼
            └── util/
                ├── ClassUtils.java                 # 클래스 로드 및 원시 타입 판정 유틸
                └── ObjectMapperFactory.java        # Jackson ObjectMapper 싱글톤 팩토리
```

---

## 🔄 HTTP 요청 처리 아키텍처 및 데이터 흐름

`elpring-web` 내에서 클라이언트 요청이 처리되는 순환 라이프사이클은 다음과 같습니다.

```
[Client Request]
       │
       ▼
 1. DispatcherServlet.service(request, response)
       │
       ├─> 2. RequestMappingHandlerMapping.getHandler(request) 호출
       │       ├─ staticHandlerRegistry (정적 주소 단일 해시 맵 검색)
       │       └─ patternHandlerRegistry (정적 매핑 누락 시 PathVariable 정규식 패턴 순회 탐색)
       │       └─ 매칭 성공 시 URI 템플릿 변수를 추출하여 request attribute에 바인딩
       │
       ▼ (실행 대상 HandlerMethod 획득 완료)
 3. RequestMappingHandlerAdapter.handle(request, response, handlerMethod) 실행
       │
       ├─> 4. HandlerMethodArgumentResolverComposite.resolveArgument() 호출 (파라미터 주입 루프)
       │       ├─ 어노테이션에 일치하는 최적의 리졸버(6종 중) 순회 선택
       │       ├─ (예) @RequestBody 인 경우 JacksonJsonHttpMessageConverter로 HTTP Body 파싱
       │       └─ (예) @RequestParam 인 경우 TypeConversionService로 알맞은 타입 자동 형변환
       │
       ├─> 5. Java Reflection API를 활용해 실제 Controller 메서드 실행 및 반환값 획득
       │
       └─> 6. 리턴 값을 Jackson ObjectMapper를 사용해 JSON 문자열로 변환 및 Response 쓰기
       │
       ▼
 [HTTP Response (200 OK, application/json)]
```

---

## 🚀 시작하기 (Getting Started)

### 1. 의존성 추가 (build.gradle)
JitPack을 활용하여 `build.gradle`에 의존성을 추가합니다.  
*(통합 `elpring-framework` 모노레포 내에서 로컬 개발을 진행할 때는 외부 라이브러리 추가 대신 `project(':elpring-web')`로 직접 프로젝트를 참조할 수 있습니다.)*

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.eello:elpring-web:v1.0.3'
}
```

### 2. ⚠️ 필수 컴파일 옵션 설정 (매우 중요)
`elpring-web`은 컨트롤러 메서드의 파라미터 이름을 기반으로 `@RequestParam`, `@PathVariable` 등의 바인딩 값을 추출합니다. 따라서 자바 컴파일 시 **파라미터 이름을 바이트코드에 남기는 옵션(`-parameters`)**을 반드시 활성화해야 합니다.

`build.gradle` 하단에 아래 설정을 추가해 주세요:

```groovy
tasks.withType(JavaCompile).configureEach {
    options.compilerArgs << "-parameters"
}
```

> **참고**: IDE(IntelliJ 등)에서 애플리케이션을 직접 실행할 경우, `Settings > Build, Execution, Deployment > Compiler > Java Compiler` 메뉴로 이동하여 **Additional command line parameters** 항목에 `-parameters`를 반드시 추가해야 합니다.

---

## 💻 사용 예제

### 1. 컨트롤러 등록
`@Controller` 어노테이션을 사용하여 빈으로 등록할 대상 컨트롤러를 선언합니다. `@Controller` 어노테이션은 이미 내부에 `@Component`를 메타 어노테이션으로 지정하고 있으므로, `@Component`를 함께 붙이지 않고 `@Controller` 단독 선언만으로 `elpring-di` 컨테이너에 의해 자동으로 스캔 및 빈으로 등록됩니다.

```java
package eello.app.controller;

import eello.elpring.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class UserController {

    public static class UserDto {
        private String name;
        private int age;
        
        public UserDto() {}
        public UserDto(String name, int age) {
            this.name = name;
            this.age = age;
        }
        public String getName() { return name; }
        public int getAge() { return age; }
    }

    // 1. @PathVariable 경로 변수 및 HttpServletRequest 주입 예제
    @GetMapping("/users/{id}")
    public String getUser(@PathVariable("id") Long userId, HttpServletRequest request) {
        String customHeader = request.getHeader("X-Client-Type");
        return "User ID: " + userId + ", Client Header: " + customHeader;
    }

    // 2. @RequestBody JSON 리스트 바인딩 및 응답 헤더 추가 예제
    @PostMapping("/users/batch")
    public List<UserDto> createUsers(@RequestBody List<UserDto> users, HttpServletResponse response) {
        response.setHeader("X-Operation-Status", "SUCCESS");
        return users; // 반환 값은 자동으로 JSON Array 문자열로 직렬화되어 응답됩니다.
    }
}
```

### 2. 톰캣 구동 및 애플리케이션 시동부 부트스트랩
`elpring-di` 컨테이너를 실행하여 빈 레지스트리를 준비한 뒤, 내장 톰캣(Tomcat)을 띄우고 서블릿 컨텍스트에 `DispatcherServlet`을 추가하여 서비스를 개시합니다.

```java
package eello.app;

import eello.elpring.di.context.AnnotationConfigApplicationContext;
import eello.elpring.web.servlet.DispatcherServlet;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import java.io.File;

public class WebApplicationBootstrap {
    public static void main(String[] args) throws Exception {
        // 1. elpring-di 컨테이너 구동 (컨트롤러 및 웹 설정 빈 자동 스캔)
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                "eello.app.controller",
                "eello.elpring.web"
        );
        context.refresh();

        // 2. 내장 톰캣 기동 및 기본 임시 경로 세팅
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        
        String baseDir = new File("build/tomcat").getAbsolutePath();
        tomcat.setBaseDir(baseDir);

        File docBase = new File("build/tomcat/webapp");
        if (!docBase.exists()) {
            docBase.mkdirs();
        }
        
        // 톰캣 Context 생성 (반드시 루트 Context Path로 구동해야 함)
        Context ctx = tomcat.addContext("", docBase.getAbsolutePath());

        // 3. DI 컨테이너에서 DispatcherServlet 빈을 조회하여 톰캣에 등록
        DispatcherServlet dispatcherServlet = context.getBean(DispatcherServlet.class);
        Wrapper wrapper = Tomcat.addServlet(ctx, "dispatcherServlet", dispatcherServlet);
        wrapper.setLoadOnStartup(1);
        ctx.addServletMappingDecoded("/*", "dispatcherServlet");

        // 4. 톰캣 기동
        tomcat.start();
        System.out.println("Elpring Web MVC Application started on port 8080!");
        tomcat.getServer().await();
    }
}
```

---

## 🚫 제약 사항 및 예외 규약

1. **컨텍스트 패스 및 서블릿 매핑 제약**
   - 경로 매칭 방식이 `request.getRequestURI()`를 기준으로 작동하므로, WAS(톰캣 등) 구동 시 **Context Path는 무조건 루트(`""`)여야 하며, DispatcherServlet의 매핑 규칙 또한 `/*`로 지정되어야** 정상적인 컨트롤러 매핑이 동작합니다.
   - 루트가 아닌 경로 매핑 설정 시 핸들러를 검색하지 못해 `404 Not Found`가 발생합니다.
2. **뷰 렌더링 미지원 (Rest-only)**
   - HTML 뷰 렌더링(ViewResolver 스펙)을 지원하지 않습니다. `@ResponseBody` 또는 `@RestController` 어노테이션 지정 여부와 상관없이 **컨트롤러 메서드의 모든 반환값은 항상 본문 직렬화(JSON 또는 텍스트)를 거쳐 응답**합니다.
3. **단일 생성자 의존성 주입**
   - 웹 컨트롤러 빈 주입 시, 오직 1개의 public 생성자만 존재해야 하며 생성자 기반 의존성 주입만 지원합니다.

---

## 🧪 테스트 코드 구동

프로젝트 빌드 및 모든 단위/통합 테스트는 다음 명령어를 통해 수행할 수 있습니다.

```bash
./gradlew test
```

- **단위 테스트**: `Mock` 프록시 요청 구조를 활용하여 개별 아규먼트 리졸버의 기능적 정상 여부 검증.
- **통합 테스트**: `DispatcherServletIntegrationTest` 클래스를 통해 내장 톰캣 서버를 실제 임의의 포트로 구동한 뒤 HTTP 통신을 거쳐 모든 아규먼트 바인딩 및 직렬화 흐름의 E2E 정상 여부를 검증합니다.
