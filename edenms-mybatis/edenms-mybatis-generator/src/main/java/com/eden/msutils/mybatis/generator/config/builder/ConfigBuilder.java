/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.eden.msutils.mybatis.generator.config.builder;


import cn.hutool.core.util.ArrayUtil;
import com.eden.msutils.mybatis.generator.InjectionConfig;
import com.eden.msutils.mybatis.generator.config.*;
import com.eden.msutils.mybatis.generator.config.po.TableInfo;
import com.eden.msutils.mybatis.generator.config.rules.NamingStrategy;
import com.eden.msutils.mybatis.generator.utils.StringUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 配置汇总 传递给文件生成工具
 *
 * @author YangHu, tangguo, hubin, Juzi
 * @since 2016-08-30
 */
public class ConfigBuilder {

    /**
     * 模板路径配置信息
     */
    private final TemplateConfig template;
    /**
     * 数据库配置
     */
    private final DataSourceConfig dataSourceConfig;

    /**
     * 数据库表信息
     */
    private List<TableInfo> tableInfoList;

    /**
     *  包配置
     */
    private PackageConfig packageConfig;
    /**
     * 策略配置
     */
    private StrategyConfig strategyConfig;
    /**
     * 全局配置信息
     */
    private GlobalConfig globalConfig;
    /**
     * 注入配置信息
     */
    private InjectionConfig injectionConfig;
    /**
     * 过滤正则
     */
    private static final Pattern REGX = Pattern.compile("[~!/@#$%^&*()+\\\\\\[\\]|{};:'\",<.>?]+");

    /**
     * 在构造器中处理配置
     *
     * @param packageConfig    包配置
     * @param dataSourceConfig 数据源配置
     * @param strategyConfig   表配置
     * @param template         模板配置
     * @param globalConfig     全局配置
     */
    public ConfigBuilder(PackageConfig packageConfig, DataSourceConfig dataSourceConfig, StrategyConfig strategyConfig,
                         TemplateConfig template, GlobalConfig globalConfig) {
        // 全局配置
        this.globalConfig = Optional.ofNullable(globalConfig).orElseGet(GlobalConfig::new);
        // 模板配置
        this.template = Optional.ofNullable(template).orElseGet(TemplateConfig::new);
        // 包配置
        this.packageConfig = packageConfig;

        // 数据源配置
        this.dataSourceConfig = dataSourceConfig;
        // 策略配置
        this.strategyConfig = Optional.ofNullable(strategyConfig).orElseGet(StrategyConfig::new);

        this.tableInfoList = getTablesInfo();



    }

    private List<TableInfo> getTablesInfo(){


        List<TableInfo> tableInfoList = dataSourceConfig.getTableInfo(packageConfig.getTableModule(), strategyConfig.isSkipView());
        tableInfoList.forEach( ti -> {
            dataSourceConfig.convertTableField(ti, strategyConfig, globalConfig);
            ti.processTable(globalConfig, strategyConfig);
            ti.handlePackage(globalConfig, strategyConfig, packageConfig);

        });

        return tableInfoList;
    }






    // ************************ 曝露方法 BEGIN*****************************



    /**
     * 表信息
     *
     * @return 所有表信息
     */
    public List<TableInfo> getTableInfoList() {
        return tableInfoList;
    }

    public ConfigBuilder setTableInfoList(List<TableInfo> tableInfoList) {
        this.tableInfoList = tableInfoList;
        return this;
    }

    /**
     * 模板路径配置信息
     *
     * @return 所以模板路径配置信息
     */
    public TemplateConfig getTemplate() {
        return this.template;
    }

    // ****************************** 曝露方法 END**********************************


    public StrategyConfig getStrategyConfig() {
        return strategyConfig;
    }


    public ConfigBuilder setStrategyConfig(StrategyConfig strategyConfig) {
        this.strategyConfig = strategyConfig;
        return this;
    }


    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }


    public ConfigBuilder setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
        return this;
    }


    public InjectionConfig getInjectionConfig() {
        return injectionConfig;
    }


    public ConfigBuilder setInjectionConfig(InjectionConfig injectionConfig) {
        this.injectionConfig = injectionConfig;
        return this;
    }


    public Set<String> getPathInfo() {
        return tableInfoList.stream().flatMap( t -> t.getPathInfo().values().stream()).collect(Collectors.toSet());
    }
}
