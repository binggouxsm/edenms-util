package com.eden.msutils.boot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = EdenmsBootProperties.PREFIX)
public class EdenmsBootProperties {

    public static final String PREFIX = "eden.msutils.boot";

    /**
     * 设置包的basePackage路径，该设置不可为空
     */
    private String basePackage = "";

    private TransactionConfig transaction = new TransactionConfig();

    @Data
    public static class TransactionConfig{
        /**
         *  设置spring jdbc事务 开启（true）或者关闭（false）
         */
        private boolean enabled = false;

        /**
         *  设置在哪一层进行保障事务完整性，默认为service层，依赖于base-package设置
         *  例如："execution (* com.bocsoft..service.*.*(..))"
         */
        private String cutLayer = "service";

    }




}
