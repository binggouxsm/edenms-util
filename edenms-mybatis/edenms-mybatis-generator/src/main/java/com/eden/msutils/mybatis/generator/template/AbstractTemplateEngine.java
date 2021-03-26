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
package com.eden.msutils.mybatis.generator.template;

import com.eden.msutils.mybatis.generator.config.StrategyConfig;
import com.eden.msutils.mybatis.generator.config.TemplateConfig;
import com.eden.msutils.mybatis.generator.InjectionConfig;
import com.eden.msutils.mybatis.generator.config.ConstVal;
import com.eden.msutils.mybatis.generator.config.GlobalConfig;
import com.eden.msutils.mybatis.generator.config.builder.ConfigBuilder;
import com.eden.msutils.mybatis.generator.config.po.TableInfo;
import com.eden.msutils.mybatis.generator.config.rules.FileType;
import com.eden.msutils.mybatis.generator.exception.MybatisGeneratorException;
import com.eden.msutils.mybatis.generator.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 模板引擎抽象类
 *
 * @author hubin
 * @since 2018-01-10
 */
public abstract class AbstractTemplateEngine {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractTemplateEngine.class);
    /**
     * 配置信息
     */
    private ConfigBuilder configBuilder;


    /**
     * 模板引擎初始化
     */
    public AbstractTemplateEngine init(ConfigBuilder configBuilder) {
        this.configBuilder = configBuilder;
        return this;
    }




    protected void writerFile(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
        if (StringUtils.isNotBlank(templatePath)) this.writer(objectMap, templatePath, outputFile);
    }

    /**
     * 将模板转化成为文件
     *
     * @param objectMap    渲染对象 MAP 信息
     * @param templatePath 模板文件
     * @param outputFile   文件生成的目录
     */
    public abstract void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception;

    /**
     * 处理输出目录
     */
    public AbstractTemplateEngine mkdirs() {
        getConfigBuilder().getPathInfo().forEach((value) -> {
            File dir = new File(value);
            if (!dir.exists()) {
                boolean result = dir.mkdirs();
                if (result) {
                    logger.debug("创建目录： [" + value + "]");
                }
            }
        });
        return this;
    }

    public AbstractTemplateEngine batchOutput() {
        try{
            List<TableInfo> tableInfoList = configBuilder.getTableInfoList();
            TemplateConfig template = configBuilder.getTemplate();
            for (TableInfo tableInfo : tableInfoList) {
                Map<String, Object> objectMap = getObjectMap(tableInfo);

                System.out.println(objectMap);
                Map<String, String> pathInfo = tableInfo.getPathInfo();


                // 自定义内容
                InjectionConfig injectionConfig = getConfigBuilder().getInjectionConfig();
                if (null != injectionConfig) {
                    injectionConfig.initTableMap(tableInfo);
                    objectMap.put("cfg", injectionConfig.getMap());
                    // :todo 自定义输出文件
//                    List<FileOutConfig> focList = injectionConfig.getFileOutConfigList();
//                    if (CollectionUtils.isNotEmpty(focList)) {
//                        for (FileOutConfig foc : focList) {
//                            if (isCreate(FileType.OTHER, foc.outputFile(tableInfo))) {
//                                writerFile(objectMap, foc.getTemplatePath(), foc.outputFile(tableInfo));
//                            }
//                        }
//                    }
                }

                // Entity.java
                String entityName = tableInfo.getEntityName();
                if (null != entityName && null != pathInfo.get(ConstVal.ENTITY_PATH)) {
                    String entityFile = String.format((pathInfo.get(ConstVal.ENTITY_PATH) + File.separator + "%s" + ConstVal.JAVA_SUFFIX), entityName);
                    if (isCreate(FileType.ENTITY, entityFile)) {
                        writerFile(objectMap, templateFilePath(template.getEntity()), entityFile);
                    }
                }

                // Mapper.java
                if (null != tableInfo.getMapperName() && null != pathInfo.get(ConstVal.MAPPER_PATH)) {
                    String mapperFile = String.format((pathInfo.get(ConstVal.MAPPER_PATH) + File.separator + tableInfo.getMapperName() + ConstVal.JAVA_SUFFIX), entityName);
                    if (isCreate(FileType.MAPPER, mapperFile)) {
                        writerFile(objectMap, templateFilePath(template.getMapper()), mapperFile);
                    }
                }
                // Mapper.xml
                if (null != tableInfo.getXmlName() && null != pathInfo.get(ConstVal.XML_PATH)) {
                    String xmlFile = String.format((pathInfo.get(ConstVal.XML_PATH) + File.separator + tableInfo.getXmlName() + ConstVal.XML_SUFFIX), entityName);
                    if (isCreate(FileType.XML, xmlFile)) {
                        writerFile(objectMap, templateFilePath(template.getXml()), xmlFile);
                    }
                }
                // Service.java
                if (null != tableInfo.getServiceName() && null != pathInfo.get(ConstVal.SERVICE_PATH)) {
                    String serviceFile = String.format((pathInfo.get(ConstVal.SERVICE_PATH) + File.separator + tableInfo.getServiceName() + ConstVal.JAVA_SUFFIX), entityName);
                    if (isCreate(FileType.SERVICE, serviceFile)) {
                        writerFile(objectMap, templateFilePath(template.getService()), serviceFile);
                    }
                }
                // ServiceImpl.java
                if (null != tableInfo.getServiceImplName() && null != pathInfo.get(ConstVal.SERVICE_IMPL_PATH)) {
                    String implFile = String.format((pathInfo.get(ConstVal.SERVICE_IMPL_PATH) + File.separator + tableInfo.getServiceImplName() + ConstVal.JAVA_SUFFIX), entityName);
                    if (isCreate(FileType.SERVICE_IMPL, implFile)) {
                        writerFile(objectMap, templateFilePath(template.getServiceImpl()), implFile);
                    }
                }
                // Controller.java
                if (null != tableInfo.getControllerName() && null != pathInfo.get(ConstVal.CONTROLLER_PATH)) {
                    String controllerFile = String.format((pathInfo.get(ConstVal.CONTROLLER_PATH) + File.separator + tableInfo.getControllerName() + ConstVal.JAVA_SUFFIX), entityName);
                    if (isCreate(FileType.CONTROLLER, controllerFile)) {
                        writerFile(objectMap, templateFilePath(template.getController()), controllerFile);
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            throw new MybatisGeneratorException("无法创建文件，请检查配置信息！");
        }
        return this;
    }

    /**
     * 渲染对象 MAP 信息
     *
     * @param tableInfo 表信息对象
     * @return ignore
     */
    public Map<String, Object> getObjectMap(TableInfo tableInfo) {
        Map<String, Object> objectMap = new HashMap<>();
        ConfigBuilder config = getConfigBuilder();
        GlobalConfig globalConfig = config.getGlobalConfig();
        StrategyConfig strategyConfig = config.getStrategyConfig();
        if (strategyConfig.isControllerMappingHyphenStyle()) {
            objectMap.put("controllerMappingHyphenStyle", config.getStrategyConfig().isControllerMappingHyphenStyle());
            objectMap.put("controllerMappingHyphen", StringUtils.camelToHyphen(tableInfo.getEntityPath()));
        }
        objectMap.put("restControllerStyle", config.getStrategyConfig().isRestControllerStyle());
        objectMap.put("config", config);
        objectMap.put("package", tableInfo.getPackageInfo());

        objectMap.put("author", globalConfig.getAuthor());
        objectMap.put("swagger2", strategyConfig.isSwagger2());
        objectMap.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        objectMap.put("table", tableInfo);

        objectMap.put("entity", tableInfo.getEntityName());
        objectMap.put("entitySerialVersionUID", config.getStrategyConfig().isEntitySerialVersionUID());
        objectMap.put("entityColumnConstant", config.getStrategyConfig().isEntityColumnConstant());
        objectMap.put("chainModel", config.getStrategyConfig().isChainModel());
        objectMap.put("entityLombokModel", config.getStrategyConfig().isEntityLombokModel());
        objectMap.put("entityBooleanColumnRemoveIsPrefix", config.getStrategyConfig().isEntityBooleanColumnRemoveIsPrefix());

        objectMap.put("enableCache", strategyConfig.isEnableCache());
        objectMap.put("baseResultMap", strategyConfig.isBaseResultMap());
        objectMap.put("baseColumnList", strategyConfig.isBaseColumnList());

        objectMap.put("diableServiceInterface", strategyConfig.isDiableServiceInterface());

        objectMap.put("superEntityClass", getSuperClassName(config.getStrategyConfig().getSuperEntityClass()));
        objectMap.put("superMapperClassPackage", config.getStrategyConfig().getSuperMapperClass());
        objectMap.put("superMapperClass", getSuperClassName(config.getStrategyConfig().getSuperMapperClass()));
        objectMap.put("superServiceClassPackage", config.getStrategyConfig().getSuperServiceClass());
        objectMap.put("superServiceClass", getSuperClassName(config.getStrategyConfig().getSuperServiceClass()));
        objectMap.put("superServiceImplClassPackage", config.getStrategyConfig().getSuperServiceImplClass());
        objectMap.put("superServiceImplClass", getSuperClassName(config.getStrategyConfig().getSuperServiceImplClass()));
        objectMap.put("superControllerClassPackage", verifyClassPacket(config.getStrategyConfig().getSuperControllerClass()));
        objectMap.put("superControllerClass", getSuperClassName(config.getStrategyConfig().getSuperControllerClass()));
        return Objects.isNull(config.getInjectionConfig()) ? objectMap : config.getInjectionConfig().prepareObjectMap(objectMap);
    }

    /**
     * 检测文件是否存在
     *
     * @return 文件是否存在
     */
    protected boolean isCreate(FileType fileType, String filePath) {
        ConfigBuilder cb = getConfigBuilder();
        // 自定义判断
        InjectionConfig ic = cb.getInjectionConfig();
//        if (null != ic && null != ic.getFileCreate()) {
//            return ic.getFileCreate().isCreate(cb, fileType, filePath);
//        }
        // 全局判断【默认】
        File file = new File(filePath);
        boolean exist = file.exists();
        if (!exist) {
            file.getParentFile().mkdirs();
        }
        return !exist || getConfigBuilder().getGlobalConfig().isFileOverride();
    }


    /**
     * 打开输出目录
     */
    public void open() {
        String outDir = getConfigBuilder().getGlobalConfig().getOutputDir();
        if (getConfigBuilder().getGlobalConfig().isOpen()
            && StringUtils.isNotBlank(outDir)) {
            try {
                String osName = System.getProperty("os.name");
                if (osName != null) {
                    if (osName.contains("Mac")) {
                        Runtime.getRuntime().exec("open " + outDir);
                    } else if (osName.contains("Windows")) {
                        Runtime.getRuntime().exec("cmd /c start " + outDir);
                    } else {
                        logger.debug("文件输出目录:" + outDir);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    /**
     * 用于渲染对象MAP信息 {@link #(TableInfo)} 时的superClassPacket非空校验
     *
     * @param classPacket ignore
     * @return ignore
     */
    private String verifyClassPacket(String classPacket) {
        return StringUtils.isBlank(classPacket) ? null : classPacket;
    }

    /**
     * 获取类名
     *
     * @param classPath ignore
     * @return ignore
     */
    private String getSuperClassName(String classPath) {
        if (StringUtils.isBlank(classPath)) {
            return null;
        }
        return classPath.substring(classPath.lastIndexOf(ConstVal.DOT) + 1);
    }


    /**
     * 模板真实文件路径
     *
     * @param filePath 文件路径
     * @return ignore
     */
    public abstract String templateFilePath(String filePath);





    public ConfigBuilder getConfigBuilder() {
        return configBuilder;
    }

    public AbstractTemplateEngine setConfigBuilder(ConfigBuilder configBuilder) {
        this.configBuilder = configBuilder;
        return this;
    }
}
