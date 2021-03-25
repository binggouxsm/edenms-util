package com.eden.msutils.mybatis.generator;

import com.eden.msutils.mybatis.generator.config.*;
import com.eden.msutils.mybatis.generator.config.builder.ConfigBuilder;
import com.eden.msutils.mybatis.generator.template.AbstractTemplateEngine;
import com.eden.msutils.mybatis.generator.template.FreemarkerTemplateEngine;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AutoGenerator {

    private GlobalConfig globalConfig;

    private InjectionConfig injectionConfig;

    private DataSourceConfig dataSourceConfig;

    private PackageConfig packageConfig;

    private StrategyConfig strategyConfig;

    private TemplateConfig templateConfig;

    private ConfigBuilder configBuilder;

    /**
     * 模板引擎
     */
    private AbstractTemplateEngine templateEngine = new FreemarkerTemplateEngine();

    public void execute(){
        if(configBuilder == null){
            configBuilder = new ConfigBuilder(packageConfig,dataSourceConfig,strategyConfig,templateConfig,globalConfig);
            if (null != injectionConfig) {
                configBuilder.setInjectionConfig(injectionConfig);
            }
        }
        templateEngine.init(configBuilder).mkdirs().batchOutput();
    }


}
