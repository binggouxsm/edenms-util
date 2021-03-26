package ${package.Service};

import ${package.Entity}.${entity};
<#if diableServiceInterface>
import ${package.Mapper}.${table.mapperName};
import org.springframework.stereotype.Service;
</#if>
<#if superServiceClassPackage??>
import ${superServiceClassPackage};
</#if>
/**
 * <p>
 * ${table.comment!} 服务类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if diableServiceInterface>
 @Service
 public class ${table.serviceName}<#if superServiceClass??> extends ${superServiceClass}<${table.mapperName}, ${entity}></#if> {

 }
<#else>
 public interface ${table.serviceName}<#if superServiceClass??> extends ${superServiceClass}<${entity}></#if> {

 }
</#if>


