package ${package.ServiceImpl};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
<#if superServiceImplClassPackage??>
import ${superServiceImplClassPackage};
<#else>
import org.springframework.beans.factory.annotation.Autowired;
</#if>
import org.springframework.stereotype.Service;

/**
 * <p>
 * ${table.comment!} 服务实现类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
public class ${table.serviceImplName}<#if superServiceImplClass??> extends ${superServiceImplClass}<${table.mapperName}, ${entity}></#if> implements ${table.serviceName} {
<#if superServiceImplClass??>
<#else>
	@Autowired
	private ${table.mapperName} ${table.mapperName?uncap_first};
</#if>
}

