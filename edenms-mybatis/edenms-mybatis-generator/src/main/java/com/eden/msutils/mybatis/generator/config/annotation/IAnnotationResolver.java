package com.eden.msutils.mybatis.generator.config.annotation;

import com.eden.msutils.mybatis.generator.config.StrategyConfig;
import com.eden.msutils.mybatis.generator.config.po.TableField;
import com.eden.msutils.mybatis.generator.config.po.TableInfo;

public interface IAnnotationResolver {

    public default void resolve(TableInfo tableInfo, StrategyConfig config){
        resolveSuperClass(tableInfo,config);
        resolveTableAnno(tableInfo,config);
        for(TableField field : tableInfo.getFields()){
            resolveTableFieldAnno(tableInfo,field,config);
        }
    }

    public void resolveSuperClass(TableInfo tableInfo, StrategyConfig config);

    public void resolveTableAnno(TableInfo tableInfo, StrategyConfig config);

    public void resolveTableFieldAnno(TableInfo tableInfo, TableField field, StrategyConfig config);

}
