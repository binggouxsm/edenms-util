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
package com.eden.msutils.mybatis.generator.config.po;

import java.io.File;
import java.util.*;


import com.eden.msutils.mybatis.generator.config.*;
import com.eden.msutils.mybatis.generator.config.rules.NamingStrategy;
import com.eden.msutils.mybatis.generator.exception.MybatisGeneratorException;
import com.eden.msutils.mybatis.generator.utils.StringUtils;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 表信息，关联到当前字段信息
 *
 * @author YangHu
 * @since 2016/8/30
 */
@Data
@Accessors(chain = true)
public class TableInfo {

    private final Set<String> importPackages = new HashSet<>();
    private boolean convert;
    private String name;
    private String moduleName;
    private String comment;
    private String entityName;
    private String mapperName;
    private String xmlName;
    private String serviceName;
    private String serviceImplName;
    private String controllerName;
    private List<TableField> fields;
    private boolean havePrimaryKey;
    /**
     * 公共字段
     */
    private List<TableField> commonFields;
    private String fieldNames;


    /**
     * 包配置详情
     */
    private Map<String, String> packageInfo = new HashMap<>();
    /**
     * 路径配置信息
     */
    private Map<String, String> pathInfo = new HashMap<>();

    public TableInfo setConvert(boolean convert) {
        this.convert = convert;
        return this;
    }

    protected TableInfo setConvert(StrategyConfig strategyConfig) {
        if (strategyConfig.startsWithTablePrefix(name) || strategyConfig.isEntityTableFieldAnnotationEnable()) {
            // 包含前缀
            this.convert = true;
        } else if (strategyConfig.isCapitalModeNaming(name)) {
            // 包含
            this.convert = false;
        } else {
            // 转换字段
            if (NamingStrategy.underline_to_camel == strategyConfig.getColumnNaming()) {
                // 包含大写处理
                if (StringUtils.containsUpperCase(name)) {
                    this.convert = true;
                }
            } else if (!entityName.equalsIgnoreCase(name)) {
                this.convert = true;
            }
        }
        return this;
    }

    public TableInfo setEntityName(StrategyConfig strategyConfig, String entityName) {
        this.entityName = entityName;
        this.setConvert(strategyConfig);
        return this;
    }

    public TableInfo setFields(List<TableField> fields) {
        this.fields = fields;

        // :todo 采集 importPackages
        return this;
    }


    public void processTable(GlobalConfig globalConfig, StrategyConfig strategyConfig){
        String tmpEntityName;
        INameConvert nameConvert = strategyConfig.getNameConvert();
        if (null != nameConvert) {
            // 自定义处理实体名称
            tmpEntityName = nameConvert.entityNameConvert(this);
        } else {
            tmpEntityName = StringUtils.capitalFirst(strategyConfig.processName(name));
        }
        if (StringUtils.isNotBlank(globalConfig.getEntityName())) {
            convert = true;
            entityName = String.format(globalConfig.getEntityName(), tmpEntityName);
        } else {
            this.setEntityName(strategyConfig, tmpEntityName);
        }
        if (StringUtils.isNotBlank(globalConfig.getMapperName())) {
            mapperName = String.format(globalConfig.getMapperName(), tmpEntityName);
        } else {
            mapperName = tmpEntityName + ConstVal.MAPPER;
        }
        if (StringUtils.isNotBlank(globalConfig.getXmlName())) {
            xmlName = String.format(globalConfig.getXmlName(), tmpEntityName);
        } else {
            xmlName = tmpEntityName + ConstVal.MAPPER;
        }
        if (StringUtils.isNotBlank(globalConfig.getServiceName())) {
            serviceName = String.format(globalConfig.getServiceName(), tmpEntityName);
        } else {
            serviceName = tmpEntityName + ConstVal.SERVICE;
        }
        if (StringUtils.isNotBlank(globalConfig.getServiceImplName())) {
            serviceImplName = String.format(globalConfig.getServiceImplName(), tmpEntityName);
        } else {
            serviceImplName = tmpEntityName + ConstVal.SERVICE_IMPL;
        }
        if (StringUtils.isNotBlank(globalConfig.getControllerName())) {
            controllerName = String.format(globalConfig.getControllerName(), tmpEntityName);
        } else {
            controllerName = tmpEntityName + ConstVal.CONTROLLER ;
        }

        checkImportPackages(globalConfig,strategyConfig);
    }


    private void checkImportPackages(GlobalConfig globalConfig, StrategyConfig strategyConfig){
        if (StringUtils.isNotBlank(strategyConfig.getSuperEntityClass())) {
            // 自定义父类
            importPackages.add(strategyConfig.getSuperEntityClass());
        }
    }


    public void handlePackage(GlobalConfig globalConfig, PackageConfig packageConfig) {
        String entityPackage = joinPackage(packageConfig.getParent(),moduleName, packageConfig.getEntity());
        String mapperPackage = joinPackage(packageConfig.getParent(),moduleName, packageConfig.getMapper());
        String xmlPackage = joinPackage(null, packageConfig.getXml(),moduleName);
        String servicePackage = joinPackage(packageConfig.getParent(),moduleName, packageConfig.getService());
        String serviceImplPackage = joinPackage(packageConfig.getParent(),moduleName, packageConfig.getServiceImpl());
        String controllerPackage = joinPackage(packageConfig.getParent(),moduleName, packageConfig.getController());

        packageInfo.put(ConstVal.ENTITY, entityPackage);
        packageInfo.put(ConstVal.MAPPER, mapperPackage);
        packageInfo.put(ConstVal.XML, xmlPackage);
        packageInfo.put(ConstVal.SERVICE, servicePackage);
        if(!globalConfig.isDiableServiceInterface()){
            packageInfo.put(ConstVal.SERVICE_IMPL, serviceImplPackage);
        }
        packageInfo.put(ConstVal.CONTROLLER, controllerPackage);

        String outputPath = globalConfig.getOutputDir();
        String xmlOutputPath = globalConfig.getXmlOutputDir();

        pathInfo.put(ConstVal.ENTITY_PATH, joinPath(outputPath,entityPackage));
        pathInfo.put(ConstVal.MAPPER_PATH, joinPath(outputPath,mapperPackage));
        pathInfo.put(ConstVal.XML_PATH, joinPath(xmlOutputPath,xmlPackage));
        pathInfo.put(ConstVal.SERVICE_PATH, joinPath(outputPath,servicePackage));
        if(!globalConfig.isDiableServiceInterface()) {
            pathInfo.put(ConstVal.SERVICE_IMPL_PATH, joinPath(outputPath, serviceImplPackage));
        }
        pathInfo.put(ConstVal.CONTROLLER_PATH, joinPath(outputPath,controllerPackage));

    }

    private String joinPackage(String parent, String moduleName, String subPackage) {
        return  StringUtils.joinPath(ConstVal.DOT, parent, moduleName, subPackage);
    }

    private String joinPath(String parentDir, String packageName){
        if (StringUtils.isBlank(parentDir)) {
            throw new MybatisGeneratorException("根目录为空，请设置OutputDir或者MapperOutputDir参数");
        }
        if (!StringUtils.endsWith(parentDir, File.separator)) {
            parentDir += File.separator;
        }
        return parentDir + packageName.replaceAll("\\.", ConstVal.BACK_SLASH + File.separator);
    }


}
