package com.eden.msutils.boot;

import com.eden.msutils.boot.handler.GlobalExceptionHandler;
import com.eden.msutils.boot.handler.GlobalResponseHandler;
import com.eden.msutils.boot.properties.EdenmsBootProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({
        EdenBootTransactionConfiguration.class
})
@ConditionalOnProperty(prefix = EdenmsBootProperties.PREFIX, name = "base-package" )
@EnableConfigurationProperties(EdenmsBootProperties.class)
public class EdenBootAutoConfiguration {

    private  EdenmsBootProperties properties;

    public EdenBootAutoConfiguration(EdenmsBootProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler(){
        return new GlobalExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalResponseHandler globalResponseHandler(){
        return new GlobalResponseHandler(properties.getBasePackage());
    }
}
