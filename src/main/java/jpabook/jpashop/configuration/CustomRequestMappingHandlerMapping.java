package jpabook.jpashop.configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected void registerHandlerMethod(Object handler, java.lang.reflect.Method method, org.springframework.web.servlet.mvc.method.RequestMappingInfo mapping) {
        // 기존 매핑 로직
        super.registerHandlerMethod(handler, method, mapping);
        
        // 추가적인 커스터마이징을 통해 동적 경로를 처리할 수 있음
        // 예: /api/v0/*/member/list
        String[] patterns = mapping.getPatternsCondition().getPatterns().toArray(new String[0]);

        for (String pattern : patterns) {

            log.info("######################################## pattern ======================= {}",pattern);

            if (pattern.contains("{company}")) {
                String newPattern = pattern.replace("{company}", "*");
                org.springframework.web.servlet.mvc.method.RequestMappingInfo newMapping = org.springframework.web.servlet.mvc.method.RequestMappingInfo.paths(newPattern).build();

                log.info("######################################## new Pattern ======================= {}",newPattern);
                super.registerHandlerMethod(handler, method, newMapping);
            }
        }
    }
}