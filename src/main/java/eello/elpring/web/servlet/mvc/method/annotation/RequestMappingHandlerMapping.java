package eello.elpring.web.servlet.mvc.method.annotation;
import eello.elpring.web.bind.annotation.Controller;
import eello.elpring.web.bind.annotation.DeleteMapping;
import eello.elpring.web.bind.annotation.GetMapping;
import eello.elpring.web.bind.annotation.PostMapping;
import eello.elpring.web.bind.annotation.PutMapping;
import eello.elpring.web.bind.annotation.RequestMapping;
import eello.elpring.web.bind.annotation.RequestMethod;
import eello.elpring.web.method.annotation.PathVariableMethodArgumentResolver;
import eello.elpring.web.method.HandlerMethod;
import eello.elpring.web.servlet.HandlerMapping;
import eello.elpring.web.servlet.mvc.RequestKey;

import eello.elpring.di.context.ApplicationContext;
import eello.elpring.di.context.ApplicationContextAware;
import eello.elpring.di.exception.BeansException;
import eello.elpring.web.servlet.HandlerExecutionChain;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
    Http Method와 URL을 기준으로 실행될 Handler를 찾아서 실행되어야할 Interceptor + Handler 를 묶은 HandlerExecutionChain을 반환
 */
public class RequestMappingHandlerMapping implements HandlerMapping, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(RequestMappingHandlerMapping.class);

    private ApplicationContext ctx;
    private Map<RequestKey, HandlerMethod> staticHandlerRegistry = new HashMap<>();
    private List<PatternRequestMapping> patternHandlerRegistry = new ArrayList<>();

    /*
        등록된 빈들 중 @Controller가 적용된 클래스를 찾고 그 중에서 @GetMapping, @PostMapping, @PutMapping, @DeleteMapping이 적용된
        메서드(HandlerMethod)를 찾아 mappingRegistry에 등록
     */
    private void initialize() {
        // getBeansWithAnnotation 으로 @Controller가 적용된 빈들을 조회
        Map<String, Object> controllers = ctx.getBeansWithAnnotation(Controller.class);

        for (Map.Entry<String, Object> controllerEntry : controllers.entrySet()) {
            String beanName = controllerEntry.getKey();
            Object bean = controllerEntry.getValue();
            Class<?> beanType = bean.getClass();

            String[] classUrl = {};
            if (beanType.isAnnotationPresent(RequestMapping.class)) {
                classUrl = beanType.getAnnotation(RequestMapping.class).value();
            }

            for (Method method : beanType.getDeclaredMethods()) {
                MethodRequestMappingInfo methodRequestMappingInfo = MethodRequestMappingInfo.of(method);
                if (methodRequestMappingInfo == null) {
                    continue;
                }

                String[] methodPaths = methodRequestMappingInfo.paths;
                RequestMethod[] requestMethods = methodRequestMappingInfo.methods;

                String[] combinePaths = combinePath(classUrl, methodPaths);

                List<RequestKey> staticHandlers = new ArrayList<>(); // PathVariable이 포함되지 않은 URL
                List<PatternRequestMapping> patternHandlers  = new ArrayList<>();
                for (String combinePath : combinePaths) {
                    for (RequestMethod requestMethod : requestMethods) {
                        if (RouteUtils.hasPathVariable(combinePath)) {
                            patternHandlers.add(PatternRequestMapping.of(requestMethod, combinePath));
                        } else {
                            staticHandlers.add(new RequestKey(combinePath, requestMethod));
                        }
                    }
                }

                // RequestKey와 HandlerMethod 매핑
                HandlerMethod handlerMethod = new HandlerMethod(method, beanName, bean, beanType);
                for (RequestKey requestKey : staticHandlers) {
                    if (staticHandlerRegistry.containsKey(requestKey)) {
                        throw new IllegalStateException("Duplicate request endpoint: " + requestKey);
                    }

                    if (log.isTraceEnabled()) {
                        log.trace("Mapped endpoint [{}] onto {}", requestKey, handlerMethod.getMethod().getName());
                    }
                    staticHandlerRegistry.put(requestKey, handlerMethod);
                }

                for (PatternRequestMapping patternHandler : patternHandlers) {
                    patternHandler.setHandlerMethod(handlerMethod);
                    
                    if (log.isTraceEnabled()) {
                        log.trace("Mapped pattern endpoint [{}] onto {}", patternHandler.getPattern().pattern(), handlerMethod.getMethod().getName());
                    }
                    patternHandlerRegistry.add(patternHandler);
                }
            }
        }
    }

    private String[] combinePath(String[] classPath, String[] methodPath) {
        Set<String> paths = new HashSet<>();
        if (classPath.length == 0) classPath = new String[]{""};
        if (methodPath.length == 0) methodPath = new String[]{""};

        for (String cp : classPath) {
            for (String mp : methodPath) {
                paths.add(combinePath(cp, mp));
            }
        }

        return paths.toArray(new String[0]);
    }

    private String combinePath(String classPath, String methodPath) {
        if (classPath.isBlank() && methodPath.isBlank()) {
            return "/";
        }

        String path = "/" +  classPath + "/" + methodPath;

        // 연속된 '/'을 하나로 치환
        path = path.replaceAll("//+", "/");

        // 경로 마지막에 '/' 이 포함된 경우 제거
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return path;
    }

    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        RequestMethod requestMethod = RequestMethod.resolve(request.getMethod());

        RequestKey requestKey = new RequestKey(requestPath, requestMethod);
        HandlerMethod handlerMethod = staticHandlerRegistry.get(requestKey);
        if (handlerMethod == null) {
            for (PatternRequestMapping patternRequestMapping : patternHandlerRegistry) {
                if (patternRequestMapping.isMatch(requestPath, requestMethod)) {
                    handlerMethod = patternRequestMapping.getHandlerMethod();

                    Map<String, String> pathVarResultMap = patternRequestMapping.extractPathVariables(requestPath);
                    request.setAttribute(
                            PathVariableMethodArgumentResolver.PATH_VARIABLE_ATTRIBUTE_KEY,
                            pathVarResultMap
                    );
                }
            }
        }

        return handlerMethod != null ? new HandlerExecutionChain(handlerMethod) : null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
        initialize();
    }

    private static class MethodRequestMappingInfo {

        private String[] paths;
        private RequestMethod[] methods;

        private MethodRequestMappingInfo(String[] paths, RequestMethod[] methods) {
            this.paths = paths;
            this.methods = methods;
        }

        public static MethodRequestMappingInfo of(Method method) {
            String[] paths;
            RequestMethod[] methods;

            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                paths = requestMapping.value();
                methods = requestMapping.method();
            } else if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping getMapping = method.getAnnotation(GetMapping.class);
                paths = getMapping.value();
                methods = new RequestMethod[]{getMapping.method()};
            } else if (method.isAnnotationPresent(PostMapping.class)) {
                PostMapping postMapping = method.getAnnotation(PostMapping.class);
                paths = postMapping.value();
                methods = new RequestMethod[]{postMapping.method()};
            } else if (method.isAnnotationPresent(PutMapping.class)) {
                PutMapping putMapping = method.getAnnotation(PutMapping.class);
                paths = putMapping.value();
                methods = new RequestMethod[]{putMapping.method()};
            } else if (method.isAnnotationPresent(DeleteMapping.class)) {
                DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
                paths = deleteMapping.value();
                methods = new RequestMethod[]{deleteMapping.method()};
            } else {
                return null;
            }

            return new MethodRequestMappingInfo(paths, methods);
        }
    }
}
