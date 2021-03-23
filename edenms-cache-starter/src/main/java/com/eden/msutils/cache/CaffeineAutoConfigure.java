package com.eden.msutils.cache;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.eden.msutils.cache.properties.CustomCacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@ConditionalOnProperty(prefix = CustomCacheProperties.PREFIX, name = "type", havingValue = "CAFFEINE")
@EnableConfigurationProperties({CustomCacheProperties.class})
public class CaffeineAutoConfigure {

    private CustomCacheProperties cacheProperties;

    @Bean("caffeineCacheManager")
    @Primary
    public CacheManager caffeineCacheManager(){
        // :todo 熟悉参数后将参数加入到Properties中
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().recordStats().initialCapacity(500)
                .expireAfterWrite(6, TimeUnit.HOURS)
                .maximumSize(1000);
        cacheManager.setAllowNullValues(true);
        cacheManager.setCaffeine(caffeine);

        return cacheManager;
    }


}
