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

import com.eden.msutils.mybatis.generator.config.converts.MySqlTypeConvert;
import com.eden.msutils.mybatis.generator.config.converts.TypeConverts;
import com.eden.msutils.mybatis.generator.config.po.TableField;
import com.eden.msutils.mybatis.generator.config.po.TableInfo;
import com.eden.msutils.mybatis.generator.config.querys.DbQueryRegistry;
import com.eden.msutils.mybatis.generator.exception.MybatisGeneratorException;
import com.eden.msutils.mybatis.generator.utils.StringUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据库配置
 *
 * @author YangHu, hcl
 * @since 2016/8/30
 */
@Getter
@Setter
@Accessors(chain = true)
public class DataSourceConfig {

    /**
     * 数据库信息查询
     */
    private IDbQuery dbQuery;
    /**
     * 数据库类型
     */
    private DbType dbType;
    /**
     * schemaName
     */
    private String schemaName;
    /**
     * 类型转换
     */
    private ITypeConvert typeConvert;
    /**
     * 关键字处理器
     *
     * @since 3.3.2
     */
    private IKeyWordsHandler keyWordsHandler;
    /**
     * 驱动连接的URL
     */
    private String url;
    /**
     * 驱动名称
     */
    private String driverName;
    /**
     * 数据库连接用户名
     */
    private String username;
    /**
     * 数据库连接密码
     */
    private String password;

    private Connection connection;

    public DataSourceConfig(String url, String driverName, String username, String password) {
        this.url = url;
        this.driverName = driverName;
        this.username = username;
        this.password = password;
        try {
            Class.forName(driverName);
            this.connection = DriverManager.getConnection(url, username, password);
            getDbQuery();
            getTypeConvert();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private IDbQuery getDbQuery() {
        if (null == dbQuery) {
            DbType dbType = getDbType();
            DbQueryRegistry dbQueryRegistry = new DbQueryRegistry();
            // 默认 MYSQL
            dbQuery = Optional.ofNullable(dbQueryRegistry.getDbQuery(dbType))
                .orElseGet(() -> dbQueryRegistry.getDbQuery(DbType.MYSQL));
        }
        return dbQuery;
    }

    /**
     * 判断数据库类型
     *
     * @return 类型枚举值
     */
    public DbType getDbType() {
        if (null == this.dbType) {
            this.dbType = this.getDbType(this.url.toLowerCase());
            if (null == this.dbType) {
                throw new MybatisGeneratorException("Unknown type of database!");
            }
        }

        return this.dbType;
    }

    /**
     * 判断数据库类型
     *
     * @param str 用于寻找特征的字符串，可以是 driverName 或小写后的 url
     * @return 类型枚举值，如果没找到，则返回 null
     */
    private DbType getDbType(String str) {
        if (str.contains(":mysql:") || str.contains(":cobar:")) {
            return DbType.MYSQL;
        } else if (str.contains(":oracle:")) {
            return DbType.ORACLE;
        } else {
            return DbType.OTHER;
        }
    }

    public ITypeConvert getTypeConvert() {
        if (null == typeConvert) {
            DbType dbType = getDbType();
            // 默认 MYSQL
            typeConvert = TypeConverts.getTypeConvert(dbType);
            if (null == typeConvert) {
                typeConvert = MySqlTypeConvert.INSTANCE;
            }
        }
        return typeConvert;
    }



    public List<TableInfo> getTableInfo(Map<String,String> tableModule, boolean isSkipView){
        String tablesSql = dbQuery.tablesSql();

        Set<String> tableNames = tableModule.keySet();
        List<TableInfo> tableList = new ArrayList<>();
        Set<String> notExistsTables = new HashSet<>(tableNames);

        if (DbType.ORACLE == dbType) {
            //oracle 默认 schema=username
            if (schemaName == null) {
                schemaName = username.toUpperCase();
            }
            tablesSql = String.format(tablesSql, schemaName);
        }

        StringBuilder sql = new StringBuilder(tablesSql);
        sql.append(" AND ").append(dbQuery.tableName()).append(" IN (")
                .append(tableNames.stream().map(tb -> "'" + tb + "'").collect(Collectors.joining(","))).append(")");

        TableInfo tableInfo;

        try (
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());
             ResultSet results = preparedStatement.executeQuery()) {
            while (results.next()) {
                final String tableName = results.getString(dbQuery.tableName());
                if (StringUtils.isBlank(tableName)) {
                    System.err.println("当前数据库为空！！！");
                    continue;
                }
                tableInfo = new TableInfo();
                tableInfo.setName(tableName);

                // 设置 Table Module
                for(String tmp: tableNames){
                    if(tmp.equalsIgnoreCase(tableName)){
                        tableInfo.setModuleName(tableModule.get(tmp));
                        notExistsTables.remove(tmp);
                    }
                }

                // 设置Table Comment
                String commentColumn = dbQuery.tableComment();
                if (StringUtils.isNotBlank(commentColumn)) {
                    String tableComment = results.getString(commentColumn);
                    if (isSkipView && "VIEW".equals(tableComment)) {
                        // 跳过视图
                        continue;
                    }
                    tableInfo.setComment(formatComment(tableComment));
                }
                tableList.add(tableInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (notExistsTables.size() > 0) {
            System.err.println("表 " + notExistsTables + " 在数据库中不存在！！！");
        }

        return tableList;
    }




    public String formatComment(String comment) {
        return StringUtils.isBlank(comment) ? ConstVal.EMPTY : comment.replaceAll("\r\n", "\t");
    }

    public void convertTableField(TableInfo tableInfo, StrategyConfig strategyConfig,GlobalConfig globalConfig) {
        List<TableField> fieldList = new ArrayList<>();
        List<TableField> commonFieldList = new ArrayList<>();
        String tableName = tableInfo.getName();
        String tableFieldsSql = dbQuery.tableFieldsSql();

        if (DbType.ORACLE == dbType) {
            tableName = tableName.toUpperCase();
            tableFieldsSql = String.format(tableFieldsSql.replace("#schema", schemaName), tableName);
        }else {
            tableFieldsSql = String.format(tableFieldsSql, tableName);
        }

        try (
                PreparedStatement preparedStatement = connection.prepareStatement(tableFieldsSql);
                ResultSet results = preparedStatement.executeQuery()) {
            while (results.next()) {
                TableField field = new TableField();
                String columnName = results.getString(dbQuery.fieldName());

                // 设置主键信息
                String key = results.getString(dbQuery.fieldKey());
                boolean isId = StringUtils.isNotBlank(key) && "PRI".equals(key.toUpperCase());
                if(isId){
                    field.setKeyFlag(true);
                    tableInfo.setHavePrimaryKey(true);
                    field.setKeyIdentityFlag(dbQuery.isKeyIdentity(results));
                }else{
                    field.setKeyFlag(false);
                }

                // 自定义字段查询
                String[] fcs = dbQuery.fieldCustom();
                if (null != fcs) {
                    Map<String, Object> customMap = new HashMap<>(fcs.length);
                    for (String fc : fcs) {
                        customMap.put(fc, results.getObject(fc));
                    }
                    field.setCustomMap(customMap);
                }

                // 处理列名信息，如果设置KeyWordsHandler进行转换
                field.setName(columnName);
                String newColumnName = columnName;
                if (keyWordsHandler != null && keyWordsHandler.isKeyWords(columnName)) {
                    System.err.printf("当前表[%s]存在字段[%s]为数据库关键字或保留字!%n", tableName, columnName);
                    field.setKeyWords(true);
                    newColumnName = keyWordsHandler.formatColumn(columnName);
                }
                field.setColumnName(newColumnName);

                // 设置列类型
                field.setType(results.getString(dbQuery.fieldType()));
                INameConvert nameConvert = strategyConfig.getNameConvert();
                if (null != nameConvert) {
                    field.setPropertyName(nameConvert.propertyNameConvert(field));
                } else {
                    field.setPropertyName(strategyConfig, strategyConfig.processColumnName(field.getName()));
                }
                field.setColumnType(typeConvert.processTypeConvert(globalConfig, field));
                String fieldCommentColumn = dbQuery.fieldComment();
                if (StringUtils.isNotBlank(fieldCommentColumn)) {
                    field.setComment(formatComment(results.getString(fieldCommentColumn)));
                }

                if (strategyConfig.includeSuperEntityColumns(field.getName())) {
                    // 通过反射跳过父类公共字段，不生成，
                    // :todo 待修改
                    commonFieldList.add(field);
                    continue;
                }
                fieldList.add(field);
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
        tableInfo.setFields(fieldList);
        tableInfo.setCommonFields(commonFieldList);

    }
}
