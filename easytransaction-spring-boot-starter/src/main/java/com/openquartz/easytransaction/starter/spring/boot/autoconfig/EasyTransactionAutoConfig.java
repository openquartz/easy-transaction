package com.openquartz.easytransaction.starter.spring.boot.autoconfig;

import com.openquartz.easytransaction.common.concurrent.DirectExecutor;
import com.openquartz.easytransaction.core.compensate.ScheduledTransactionCompensate;
import com.openquartz.easytransaction.core.compensate.TransactionCompensateFactory;
import com.openquartz.easytransaction.core.compensate.TransactionCompensateFactoryImpl;
import com.openquartz.easytransaction.core.compensate.property.TransactionProperties;
import com.openquartz.easytransaction.core.generator.DefaultGlobalTransactionIdGeneratorImpl;
import com.openquartz.easytransaction.core.generator.GlobalTransactionIdGenerator;
import com.openquartz.easytransaction.core.transaction.TransactionSupport;
import com.openquartz.easytransaction.core.trigger.TccTriggerEngine;
import com.openquartz.easytransaction.core.trigger.TccTriggerEngineImpl;
import com.openquartz.easytransaction.repository.api.TransactionCertificateRepository;
import com.openquartz.easytransaction.repository.jdbc.JdbcTransactionCertificateRepositoryImpl;
import com.openquartz.easytransaction.starter.aop.TccAnnotationAdvisor;
import com.openquartz.easytransaction.starter.aop.TccTryMethodInterceptor;
import com.openquartz.easytransaction.starter.spring.boot.autoconfig.properties.EasyTransactionProperties;
import com.openquartz.easytransaction.starter.spring.boot.autoconfig.properties.EasyTransactionProperties.EasyTransactionDataSourceProperties;
import com.openquartz.easytransaction.starter.transaction.SpringTransactionSupport;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Configuration
@EnableConfigurationProperties({EasyTransactionProperties.class})
public class EasyTransactionAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public GlobalTransactionIdGenerator globalTransactionIdGenerator() {
        return new DefaultGlobalTransactionIdGeneratorImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionSupport transactionSupport() {
        return new SpringTransactionSupport();
    }

    @Bean
    @ConditionalOnMissingBean(type = "localStorageJdbcTemplate", value = JdbcTemplate.class)
    public JdbcTemplate jdbcTemplate(EasyTransactionProperties easyTransactionProperties,
        Environment environment,
        @Autowired(required = false) DataSource dataSource) {
        if (dataSource != null) {
            return new JdbcTemplate(dataSource);
        }
        return new JdbcTemplate(newLocalStorageDataSource(easyTransactionProperties.getDataSource(), environment));
    }

    private DataSource newLocalStorageDataSource(EasyTransactionDataSourceProperties easyFileLocalProperties,
        Environment environment) {

        Iterable<ConfigurationPropertySource> sources = ConfigurationPropertySources
            .get(environment);
        Binder binder = new Binder(sources);
        Properties properties = binder.bind(EasyTransactionProperties.PREFIX + ".datasource", Properties.class).get();

        DataSource dataSource = buildDataSource(easyFileLocalProperties);
        buildDataSourceProperties(dataSource, properties);
        return dataSource;
    }

    private DataSource buildDataSource(EasyTransactionDataSourceProperties easyFileLocalProperties) {
        String dataSourceType = easyFileLocalProperties.getType();
        try {
            Class<? extends DataSource> type = (Class<? extends DataSource>) Class.forName(dataSourceType);
            String driverClassName = easyFileLocalProperties.getDriverClassName();
            String url = easyFileLocalProperties.getUrl();
            String username = easyFileLocalProperties.getUsername();
            String password = easyFileLocalProperties.getPassword();

            return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .type(type)
                .build();

        } catch (ClassNotFoundException e) {
            log.error("buildDataSource error", e);
            throw new IllegalStateException(e);
        }
    }

    private void buildDataSourceProperties(DataSource dataSource, Map<Object, Object> dsMap) {
        try {
            BeanUtils.copyProperties(dataSource, dsMap);
        } catch (Exception e) {
            log.error("error copy properties", e);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionCertificateRepository transactionCertificateRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcTransactionCertificateRepositoryImpl(jdbcTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public Executor triggerExecutor() {
        return new DirectExecutor();
    }

    @Bean
    @ConditionalOnMissingBean
    public TccTriggerEngine tccTrigger(
        Executor triggerExecutor,
        TransactionSupport transactionSupport,
        TransactionCertificateRepository transactionCertificateRepository) {
        return new TccTriggerEngineImpl(triggerExecutor, transactionSupport, transactionCertificateRepository);
    }

    @Bean
    @Role(value = BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor fileExportExecutorAnnotationAdvisor(TccTriggerEngine tccTriggerEngine,
        GlobalTransactionIdGenerator globalTransactionIdGenerator,
        TransactionSupport transactionSupport,
        TransactionCertificateRepository transactionCertificateRepository,
        EasyTransactionProperties easyTransactionProperties
    ) {
        TccTryMethodInterceptor interceptor = new TccTryMethodInterceptor(tccTriggerEngine,
            globalTransactionIdGenerator,
            transactionSupport, transactionCertificateRepository);
        TccAnnotationAdvisor advisor = new TccAnnotationAdvisor(interceptor);
        advisor.setOrder(easyTransactionProperties.getTransactionAdvisorOrder());
        return advisor;
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionCompensateFactory transactionCompensateFactory(TccTriggerEngine tccTriggerEngine,
        EasyTransactionProperties easyTransactionProperties,
        TransactionCertificateRepository transactionCertificateRepository) {

        TransactionProperties transactionProperties = buildTransactionProperties(easyTransactionProperties);

        return new TransactionCompensateFactoryImpl(tccTriggerEngine, transactionProperties,
            transactionCertificateRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    public ScheduledTransactionCompensate scheduledTransactionCompensate(
        TransactionCertificateRepository transactionCertificateRepository,
        EasyTransactionProperties easyTransactionProperties,
        TransactionCompensateFactory transactionCompensateFactory) {

        TransactionProperties transactionProperties = buildTransactionProperties(easyTransactionProperties);

        return new ScheduledTransactionCompensate(transactionCertificateRepository, transactionProperties,
            transactionCompensateFactory);
    }

    private static TransactionProperties buildTransactionProperties(
        EasyTransactionProperties easyTransactionProperties) {
        TransactionProperties transactionProperties = new TransactionProperties();
        transactionProperties.setMaxTransactionTimeout(easyTransactionProperties.getMaxTransactionTimeout());
        transactionProperties.setCompensateRetryCount(easyTransactionProperties.getCompensate().getRetryCount());
        transactionProperties.setCompensateBackOffHours(easyTransactionProperties.getCompensate().getBackoffHours());
        transactionProperties.setCompensateOffset(easyTransactionProperties.getCompensate().getOffset());
        transactionProperties.setCompensateInitDelay(easyTransactionProperties.getCompensate().getInitDelay());
        transactionProperties.setCompensateRecoveryDelay(easyTransactionProperties.getCompensate().getRecoveryDelay());
        return transactionProperties;
    }

}
