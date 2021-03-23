package com.eden.msutils.cache.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = CustomCacheProperties.PREFIX)
public class CustomCacheProperties {

    public static final String PREFIX = "eden.msutils.cache";

    /**
     * 目前只支持 REDIS 和 CAFFEINE ！
     * CAFFEINE 只用于项目的开发环境或者演示环境使用，  生产环境请用redis！！！
     */
    private CacheType type = CacheType.CAFFEINE;
}
