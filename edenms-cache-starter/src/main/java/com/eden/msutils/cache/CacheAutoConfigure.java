package com.eden.msutils.cache;


import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@EnableCaching
@Import({
        CaffeineAutoConfigure.class
})
public class CacheAutoConfigure {

    private static final String  COLON = ":";

    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(COLON);
            sb.append(method.getName());
            for (Object obj : objects) {
                if (obj != null) {
                    sb.append(COLON);
                    sb.append(obj.toString());
                }
            }
            return sb.toString();
        };
    }
}
