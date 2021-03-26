package com.eden.msutils.mybatis.generator.config.annotation;

import com.eden.msutils.mybatis.generator.config.StrategyConfig;
import com.eden.msutils.mybatis.generator.config.po.TableField;
import com.eden.msutils.mybatis.generator.config.po.TableInfo;
import com.eden.msutils.mybatis.generator.utils.StringUtils;


public class TkMapperResolver implements IAnnotationResolver{

    @Override
    public void resolveSuperClass(TableInfo tableInfo, StrategyConfig config) {
        if(StringUtils.isBlank(config.getSuperMapperClass())){
            config.setSuperMapperClass("tk.mybatis.mapper.common.Mapper");
        }
    }

    public void resolveTableAnno(TableInfo tableInfo, StrategyConfig config){
        if (tableInfo.isConvert()) {
            tableInfo.getImportPackages().add("javax.persistence.Table");

            String anno = String.format("@Table( name= \"%s\")",tableInfo.getName());
            tableInfo.getAnnotations().add(anno);
        }
    }

    public void resolveTableFieldAnno(TableInfo tableInfo, TableField field, StrategyConfig config){
        if(field.isKeyFlag()){
            tableInfo.getImportPackages().add("javax.persistence.Id");
            field.getAnnotations().add("@Id");
            if(field.isKeyIdentityFlag()){
                tableInfo.getImportPackages().add("tk.mybatis.mapper.annotation.KeySql");
                field.getAnnotations().add("@KeySql(useGeneratedKeys = true)");
            }
        }
        if(field.isConvert()){
            tableInfo.getImportPackages().add("javax.persistence.Column");
            String anno = String.format("@Column( name= \"%s\")",field.getName());
            field.getAnnotations().add(anno);
        }
        if(StringUtils.isNotBlank(config.getVersionFieldName()) && config.getVersionFieldName().equals(field.getPropertyName())){
            tableInfo.getImportPackages().add("javax.persistence.Version");
            field.getAnnotations().add("@Version");
        }
        if(field.getColumnType() != null && StringUtils.isNotBlank(field.getColumnType().getPkg())){
            tableInfo.getImportPackages().add(field.getColumnType().getPkg());
        }
    }

}
