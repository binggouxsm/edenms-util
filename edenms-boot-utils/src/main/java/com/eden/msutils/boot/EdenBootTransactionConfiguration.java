package com.eden.msutils.boot;

import com.eden.msutils.boot.properties.EdenmsBootProperties;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.Collections;

@ConditionalOnProperty(prefix = EdenmsBootProperties.PREFIX, name = "transaction.enabled",havingValue = "true")
@EnableTransactionManagement
@EnableConfigurationProperties(EdenmsBootProperties.class)
public class EdenBootTransactionConfiguration {

    private EdenmsBootProperties properties;

    public EdenBootTransactionConfiguration(EdenmsBootProperties properties) {
        this.properties = properties;
    }


    /**
     * 可传播事务配置
     */
    private static final String[] DEFAULT_REQUIRED_METHOD_RULE_TRANSACTION_ATTRIBUTES = {
            "add*" ,
            "save*" ,
            "insert*" ,
            "delete*" ,
            "update*" ,
            "edit*" ,
            "batch*" ,
            "create*" ,
            "remove*" ,
            "*"
    };

    /**
     * 默认的只读事务
     */
    private static final String[] DEFAULT_READ_ONLY_METHOD_RULE_TRANSACTION_ATTRIBUTES = {
            "get*" ,
            "count*" ,
            "find*" ,
            "query*" ,
            "select*" ,
            "list*"
    };

    /**
     * 配置事务拦截器
     *
     * @param transactionManager : 事务管理器
     */
    public TransactionInterceptor customizeTransactionInterceptor (TransactionManager transactionManager ) {
        NameMatchTransactionAttributeSource transactionAttributeSource = new NameMatchTransactionAttributeSource();
        RuleBasedTransactionAttribute  readOnly = this.readOnlyTransactionRule();
        RuleBasedTransactionAttribute  required = this.requiredTransactionRule();
        // 默认的只读事务配置
        for ( String methodName : DEFAULT_READ_ONLY_METHOD_RULE_TRANSACTION_ATTRIBUTES ) {
            transactionAttributeSource.addTransactionalMethod( methodName , readOnly );
        }
        // 默认的传播事务配置
        for ( String methodName : DEFAULT_REQUIRED_METHOD_RULE_TRANSACTION_ATTRIBUTES ) {
            transactionAttributeSource.addTransactionalMethod( methodName , required );
        }

        return new TransactionInterceptor( transactionManager , transactionAttributeSource );
    }
    /**
     * 切面拦截规则
     *
     * @param transactionManager : 事务管理器
     */
    @Bean
    public AspectJExpressionPointcutAdvisor pointcutAdvisor(TransactionManager transactionManager ){

        String basePackage = properties.getBasePackage();
        String cutLayer = properties.getTransaction().getCutLayer();
        String DEFAULT_POINT_CUT = "execution (* "+ basePackage +".."+cutLayer+".*.*(..))";

        AspectJExpressionPointcutAdvisor pointcutAdvisor = new AspectJExpressionPointcutAdvisor();
        pointcutAdvisor.setAdvice(customizeTransactionInterceptor(transactionManager));
        pointcutAdvisor.setExpression(DEFAULT_POINT_CUT);
        return pointcutAdvisor;
    }

    /**
     * 支持当前事务;如果不存在创建一个新的
     * {@link org.springframework.transaction.annotation.Propagation#REQUIRED}
     */
    private RuleBasedTransactionAttribute requiredTransactionRule () {
        RuleBasedTransactionAttribute required = new RuleBasedTransactionAttribute();
        required.setRollbackRules( Collections.singletonList( new RollbackRuleAttribute( Exception.class ) ) );
        required.setPropagationBehavior( TransactionDefinition.PROPAGATION_REQUIRED );
        required.setTimeout( TransactionDefinition.TIMEOUT_DEFAULT );
        return required;
    }

    /**
     * 只读事务
     * {@link org.springframework.transaction.annotation.Propagation#NOT_SUPPORTED}
     */
    private RuleBasedTransactionAttribute readOnlyTransactionRule () {
        RuleBasedTransactionAttribute readOnly = new RuleBasedTransactionAttribute();
        readOnly.setReadOnly( true );
        readOnly.setPropagationBehavior( TransactionDefinition.PROPAGATION_NOT_SUPPORTED );
        return readOnly;
    }


}
