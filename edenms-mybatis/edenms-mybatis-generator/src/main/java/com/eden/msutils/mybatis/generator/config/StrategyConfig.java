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
package com.eden.msutils.mybatis.generator.config;

import com.eden.msutils.mybatis.generator.config.annotation.AnnotationType;
import com.eden.msutils.mybatis.generator.config.annotation.IAnnotationResolver;
import com.eden.msutils.mybatis.generator.config.annotation.TkMapperResolver;
import com.eden.msutils.mybatis.generator.config.rules.NamingStrategy;
import com.eden.msutils.mybatis.generator.utils.StringUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 策略配置项
 *
 * @author YangHu, tangguo, hubin
 * @since 2016/8/30
 */
@Data
@Accessors(chain = true)
public class StrategyConfig {
    /**
     * 是否大写命名
     */
    private boolean isCapitalMode = false;
    /**
     * 是否跳过视图
     */
    private boolean skipView = false;
    /**
     * 名称转换
     */
    private INameConvert nameConvert;
    /**
     * 数据库表映射到实体的命名策略
     */
    private NamingStrategy naming = NamingStrategy.underline_to_camel;
    /**
     * 数据库表字段映射到实体的命名策略
     * <p>未指定按照 naming 执行</p>
     */
    private NamingStrategy columnNaming = NamingStrategy.underline_to_camel;
    /**
     * 表前缀
     */
    @Setter(AccessLevel.NONE)
    private final Set<String> tablePrefix = new HashSet<>();
    /**
     * 字段前缀
     */
    @Setter(AccessLevel.NONE)
    private final Set<String> fieldPrefix = new HashSet<>();
    /**
     * 自定义继承的Entity类全称，带包名
     */
    @Setter(AccessLevel.NONE)
    private String superEntityClass;
    /**
     * 自定义基础的Entity类，公共字段
     */
    @Setter(AccessLevel.NONE)
    private final Set<String> superEntityColumns = new HashSet<>();
    /**
     * 自定义继承的Mapper类全称，带包名
     */
    private String superMapperClass;
    /**
     * 自定义继承的Service类全称，带包名
     */
    private String superServiceClass;
    /**
     * 自定义继承的ServiceImpl类全称，带包名
     */
    private String superServiceImplClass;
    /**
     * 自定义继承的Controller类全称，带包名
     */
    private String superControllerClass;

    /**
     * 实体是否生成 serialVersionUID
     */
    private boolean entitySerialVersionUID = true;
    /**
     * 【实体】是否生成字段常量（默认 false）<br>
     * -----------------------------------<br>
     * public static final String ID = "test_id";
     */
    private boolean entityColumnConstant = false;

    /**
     * 开启 swagger2 模式
     */
    private boolean swagger2 = true;


    /**
     * 【实体】是否为链式模型（默认 false）<br>
     * -----------------------------------<br>
     * public User setName(String name) { this.name = name; return this; }
     *
     * @since 3.3.2
     */
    private boolean chainModel = false;

    /**
     * 【实体】是否为lombok模型（默认 true）<br>
     * <a href="https://projectlombok.org/">document</a>
     */
    private boolean entityLombokModel = true;
    /**
     * Boolean类型字段是否移除is前缀（默认 false）<br>
     * 比如 : 数据库字段名称 : 'is_xxx',类型为 : tinyint. 在映射实体的时候则会去掉is,在实体类中映射最终结果为 xxx
     */
    private boolean entityBooleanColumnRemoveIsPrefix = false;

    /**
     * 是否在xml中添加二级缓存配置
     */
    private boolean enableCache = false;

    /**
     * 开启 BaseResultMap
     */
    private boolean baseResultMap = true;

    /**
     * 开启 baseColumnList
     */
    private boolean baseColumnList = false;

    /**
     *  不生成Service 接口，直接生成Service实现类
     */
    private boolean diableServiceInterface = false;


    /**
     * 生成 <code>@RestController</code> 控制器
     * <pre>
     *      <code>@Controller</code> -> <code>@RestController</code>
     * </pre>
     */
    private boolean restControllerStyle = true;
    /**
     * 驼峰转连字符
     * <pre>
     *      <code>@RequestMapping("/managerUserActionHistory")</code> -> <code>@RequestMapping("/manager-user-action-history")</code>
     * </pre>
     */
    private boolean controllerMappingHyphenStyle = false;
    /**
     * 是否生成实体时，生成字段注解
     */
    private boolean entityTableFieldAnnotationEnable = false;
    /**
     * 乐观锁属性名称
     */
    private String versionFieldName;

    private AnnotationType annotationType = AnnotationType.TKMAPPER;

    /**
     *   使用哪种类型的注解
     */
    @Setter(AccessLevel.NONE)
    private IAnnotationResolver annotationResolver;

    public IAnnotationResolver getAnnotationResolver(){
        // :todo 待扩展  MYBATIS_PLUS
        return new TkMapperResolver();
    }





    /**
     * 设置实体父类
     *
     * @param superEntityClass 类全名称
     * @return this
     */
    public StrategyConfig setSuperEntityClass(String superEntityClass) {
        this.superEntityClass = superEntityClass;
        return this;
    }


    /**
     * <p>
     * 设置实体父类，该设置自动识别公共字段<br/>
     * 属性 superEntityColumns 改配置无需再次配置
     * </p>
     * <p>
     * 注意！！字段策略要在设置实体父类之前有效
     * </p>
     *
     * @param clazz 实体父类 Class
     * @return
     */
    public StrategyConfig setSuperEntityClass(Class<?> clazz) {
        this.superEntityClass = clazz.getName();
        return this;
    }

    /**
     * <p>
     * 设置实体父类，该设置自动识别公共字段<br/>
     * 属性 superEntityColumns 改配置无需再次配置
     * </p>
     *
     * @param clazz        实体父类 Class
     * @param columnNaming 字段命名策略
     * @return
     */
    public StrategyConfig setSuperEntityClass(Class<?> clazz, NamingStrategy columnNaming) {
        this.columnNaming = columnNaming;
        this.superEntityClass = clazz.getName();
        return this;
    }

    public StrategyConfig setSuperServiceClass(Class<?> clazz) {
        this.superServiceClass = clazz.getName();
        return this;
    }

    public StrategyConfig setSuperServiceClass(String superServiceClass) {
        this.superServiceClass = superServiceClass;
        return this;
    }

    public StrategyConfig setSuperServiceImplClass(Class<?> clazz) {
        this.superServiceImplClass = clazz.getName();
        return this;
    }

    public StrategyConfig setSuperServiceImplClass(String superServiceImplClass) {
        this.superServiceImplClass = superServiceImplClass;
        return this;
    }

    public StrategyConfig setSuperControllerClass(Class<?> clazz) {
        this.superControllerClass = clazz.getName();
        return this;
    }

    public StrategyConfig setSuperControllerClass(String superControllerClass) {
        this.superControllerClass = superControllerClass;
        return this;
    }


    /***************************************业务内容**********************************************/
    /**
     * 处理字段名称
     *
     * @return 根据策略返回处理后的名称
     */
    public String processName(String name) {
        return processName(name, naming, tablePrefix);
    }

    /**
     * 处理字段名称
     *
     * @return 根据策略返回处理后的名称
     */
    public String processColumnName(String name) {
        return processName(name, columnNaming, fieldPrefix);
    }


    /**
     * 处理表/字段名称
     *
     * @param name     ignore
     * @param strategy ignore
     * @param prefix   ignore
     * @return 根据策略返回处理后的名称
     */
    private String processName(String name, NamingStrategy strategy, Set<String> prefix) {
        String propertyName;
        if (prefix.size() > 0) {
            if (strategy == NamingStrategy.underline_to_camel) {
                // 删除前缀、下划线转驼峰
                propertyName = StringUtils.removePrefixAndCamel(name, prefix);
            } else {
                // 删除前缀
                propertyName = StringUtils.removePrefix(name, prefix);
            }
        } else if (strategy == NamingStrategy.underline_to_camel) {
            // 下划线转驼峰
            propertyName = StringUtils.underlineToCamel(name);
        } else {
            // 不处理
            propertyName = name;
        }
        return propertyName;
    }

    /**
     * 大写命名、字段符合大写字母数字下划线命名
     *
     * @param word 待判断字符串
     */
    public boolean isCapitalModeNaming(String word) {
        return isCapitalMode && StringUtils.isCapitalMode(word);
    }

    /**
     * 表名称匹配表前缀
     *
     * @param tableName 表名称
     * @since 3.3.2
     */
    public boolean startsWithTablePrefix(String tableName) {
        return getTablePrefix().stream().anyMatch(tableName::startsWith);
    }


    public boolean includeSuperEntityColumns(String fieldName) {
        // 公共字段判断忽略大小写【 部分数据库大小写不敏感 】
        return superEntityColumns.stream().anyMatch(e -> e.equalsIgnoreCase(fieldName));
    }

    /**
     * <p>
     * 父类 Class 反射属性转换为公共字段
     * </p>
     *
     * @param clazz 实体父类 Class
     */
    protected void convertSuperEntityColumns(Class<?> clazz) {
        List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
        this.superEntityColumns.addAll(fields.stream().map(field -> {
            // :todo 读取父类Annotation 去获取对应ColumName
//            TableId tableId = field.getAnnotation(TableId.class);
//            if (tableId != null && StringUtils.isNotBlank(tableId.value())) {
//                return tableId.value();
//            }
//            TableField tableField = field.getAnnotation(TableField.class);
//            if (tableField != null && StringUtils.isNotBlank(tableField.value())) {
//                return tableField.value();
//            }
            if (null == columnNaming || columnNaming == NamingStrategy.no_change) {
                return field.getName();
            }
            return StringUtils.camelToUnderline(field.getName());
        }).collect(Collectors.toSet()));
    }



}
