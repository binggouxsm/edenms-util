package com.eden.msutils.mybatis.generator.config;

import com.eden.msutils.mybatis.generator.config.rules.DateType;

import com.eden.msutils.mybatis.generator.config.rules.IdType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 全局配置
 *
 * @author hubin
 * @since 2016-12-02
 */
@Data
@Accessors(chain = true)
public class GlobalConfig {

    /**
     * 生成文件的输出目录【默认 D 盘根目录】
     */
    private String outputDir = "D://";

    /**
     *  生成Mapper文件的输出目录【默认 D 盘根目录】
     */
    private String xmlOutputDir = "D://";

    /**
     * 是否覆盖已有文件
     */
    private boolean fileOverride = false;

    /**
     * 是否打开输出目录
     */
    private boolean open = true;

    /**
     * 是否在xml中添加二级缓存配置
     */
    private boolean enableCache = false;

    /**
     * 开发人员
     */
    private String author;

    /**
     *  不生成Service 接口，直接生成实现类
     */
    private boolean diableServiceInterface = false;

    /**
     * 开启 swagger2 模式
     */
    private boolean swagger2 = false;


    /**
     * 开启 BaseResultMap
     */
    private boolean baseResultMap = true;

    /**
     * 时间类型对应策略
     */
    private DateType dateType = DateType.TIME_PACK;

    /**
     * 开启 baseColumnList
     */
    private boolean baseColumnList = false;
    /**
     * 各层文件名称方式，例如： %sAction 生成 UserAction
     * %s 为占位符
     */
    private String entityName;
    private String mapperName;
    private String xmlName;
    private String serviceName;
    private String serviceImplName;
    private String controllerName;
    /**
     * 指定生成的主键的ID类型
     */
    private IdType idType;
}
