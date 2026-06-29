# elpring-web 패키지 구조 및 분류 규칙

이 문서는 `elpring-web` 프로젝트의 패키지 구조 일관성을 유지하고, 향후 추가되는 클래스들이 올바른 위치에 배치되도록 돕기 위한 분류 규칙을 정의합니다.

---

## 📂 패키지 구조 요약

```
src/main/java/eello/elpring/web/
├── bind/                     # 데이터 바인딩 및 타입 변환
│   ├── annotation/           # 매핑 및 요청 바인딩용 어노테이션
│   ├── convert/              # 타입 변환기 인터페이스 및 구현체
│   └── support/              # 바인딩 보조 서비스
├── method/                   # 핸들러 메서드 및 파라미터 해석
│   ├── annotation/           # 어노테이션 기반 파라미터 리졸버 구현체
│   └── support/              # 아규먼트 리졸버 명세 및 복합체
├── servlet/                  # 서블릿 기반 핵심 웹 MVC 프레임워크
│   ├── mvc/                  # MVC 패턴 관련 컴포넌트 및 키 매핑
│   │   └── method/annotation/ # 어노테이션 기반 매핑/어댑터 구현체
│   └── (Servlet Core)        # DispatcherServlet, HandlerMapping, HandlerAdapter 등
├── config/                   # 수동 빈 설정을 위한 @Configuration 컴포넌트
├── http/                     # HTTP 관련 핵심 타입 및 유틸리티
│   └── converter/            # HTTP 메시지 바디 변환기 인터페이스 및 구현체
├── exception/                # 웹 계층 공통 예외 클래스
├── util/                     # ObjectMapper 등 웹 유틸리티
└── inbox/                    # 임시 작업용 Inbox 패키지
```

---

## 🔍 패키지별 상세 역할 및 배치 규칙

### 1. `bind` (데이터 바인딩 및 변환)
클라이언트의 HTTP 요청 파라미터나 바이트 스트림을 Java 객체/타입으로 바인딩하고 변환하는 역할을 담당합니다.
* **`bind.annotation`**: 컨트롤러 및 요청 파라미터 매핑을 위해 클라이언트가 사용하는 어노테이션을 위치시킵니다.
  * *예시*: `@Controller`, `@RequestMapping`, `@RequestParam`, `@GetMapping` 등
* **`bind.convert`**: 문자열 요청 파라미터를 Java 타입(기본형, 배열, 컬렉션, 객체 등)으로 역직렬화 및 변환하기 위한 컨버터 인터페이스 및 구현체들을 위치시킵니다.
  * *예시*: `TypeConverter` (인터페이스), `PrimitiveTypeConverter`, `ArrayTypeConverter` 등
* **`bind.support`**: 타입 변환기들의 등록 및 탐색 작업을 보조하는 서비스 클래스를 위치시킵니다.
  * *예시*: `RequestParamConversionService`

### 2. `method` (핸들러 메서드 및 파라미터解析)
요청을 처리할 대상 컨트롤러의 메서드(`HandlerMethod`) 정보를 파싱하고, 메서드 파라미터(`MethodParameter`)의 아규먼트 값을 해석하는 책임을 가집니다.
* **`method`**: 실행 가능한 메서드 정보와 파라미터 메타데이터 정보를 저장하는 핵심 구조를 위치시킵니다.
  * *예시*: `HandlerMethod`, `MethodParameter`
* **`method.support`**: 아규먼트 리졸버 인터페이스 및 이들의 합성 구조를 위치시킵니다.
  * *예시*: `HandlerMethodArgumentResolver` (인터페이스), `HandlerMethodArgumentResolverComposite`
* **`method.annotation`**: 특정 파라미터 타입이나 어노테이션을 해석해 객체를 채워주는 구체적인 리졸버 구현체들을 위치시킵니다.
  * *예시*: `RequestParamMethodArgumentResolver`, `ServletRequestMethodArgumentResolver`, `RequestBodyMethodArgumentResolver` 등

### 3. `servlet` (서블릿 MVC 프레임워크 엔진)
톰캣 등 서블릿 컨테이너와 직접 연동되는 DispatcherServlet과 핵심 MVC 흐름 제어 컴포넌트들이 위치합니다.
* **`servlet`**: 프런트 컨트롤러 패턴을 수행하는 핵심 서블릿 및 최상위 MVC 인터페이스들을 위치시킵니다.
  * *예시*: `DispatcherServlet`, `HandlerMapping` (인터페이스), `HandlerAdapter` (인터페이스), `HandlerExecutionChain`
* **`servlet.mvc`**: MVC 매핑이나 비즈니스 흐름을 위한 모델/키 객체들을 위치시킵니다.
  * *예시*: `RequestKey`
* **`servlet.mvc.method.annotation`**: 어노테이션 기반(`@RequestMapping` 등)으로 요청 경로와 컨트롤러 메서드를 매핑하고 실행하는 구체 클래스들을 위치시킵니다.
  * *예시*: `RequestMappingHandlerMapping`, `RequestMappingHandlerAdapter`

### 4. `config` (애플리케이션 설정)
클래스 경로 스캔 방식 대신 명시적으로 수동 빈을 등록하기 위한 빈 설정 정보들이 위치합니다.
* *예시*: `DispatcherServletConfig`, `HandlerMappingConfig`, `ArgumentResolverConfig`, `TypeConverterConfig`

### 5. `http` (HTTP 관련 핵심 타입 및 컨버전)
HTTP 요청/응답 메시지 바디의 직렬화/역직렬화 및 미디어 타입 등을 처리하는 구조가 위치합니다.
* **`http`**: HTTP 명세 및 공통 타입들을 위치시킵니다.
  * *예시*: `MediaType`
* **`http.converter`**: HTTP 바디 내용을 특정 객체로 또는 그 반대로 변환하는 메시지 컨버터 인터페이스 및 구현체들을 위치시킵니다.
  * *예시*: `HttpMessageConverter`, `GenericHttpMessageConverter`, `JacksonJsonHttpMessageConverter`, `StringHttpMessageConverter`

### 6. `exception` (예외)
웹 동작 도중 발생할 수 있는 에러 상황에 대한 공통/커스텀 예외 클래스들이 위치합니다.

### 7. `util` (유틸리티)
특정 도메인 논리에 종속되지 않는 공통 기술성 유틸리티 클래스들이 위치합니다.
* *예시*: `ObjectMapperFactory`, `ClassUtils`

---

## 📥 Inbox (임시 보관소) 사용 및 분류 수칙

새로운 클래스를 추가하거나 개발 중일 때, **이 클래스가 어느 패키지에 가야 할지 모호하거나 우선 구현에 집중하고 싶을 경우** 아래 패키지를 임시 작업 공간으로 활용합니다.

> **임시 패키지**: `eello.elpring.web.inbox`

### ⚠️ Inbox 정리 및 분류 프로세스
1. 새로운 클래스는 `inbox` 패키지에 편하게 생성하여 작업합니다.
2. 기능 구현이 일단락되거나 테스트를 작성하는 단계가 되면, 위의 **[패키지별 상세 역할 및 배치 규칙]**을 참고하여 해당 클래스의 책임을 분류합니다.
3. 알맞은 패키지(예: `bind.convert`, `method.annotation` 등)로 클래스를 이동(`git mv` 혹은 IDE 리팩토링 기능 사용)하고, 패키지 및 임포트 선언을 갱신합니다.
4. 리팩토링 후 빌드 및 전체 테스트(`./gradlew test`)가 여전히 통과하는지 반드시 확인합니다.
