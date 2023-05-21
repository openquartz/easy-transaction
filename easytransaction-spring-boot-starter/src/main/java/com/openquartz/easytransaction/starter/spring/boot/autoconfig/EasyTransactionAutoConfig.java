package com.openquartz.easytransaction.starter.spring.boot.autoconfig;

import com.openquartz.easytransaction.core.generator.DefaultGlobalTransactionIdGeneratorImpl;
import com.openquartz.easytransaction.core.generator.GlobalTransactionIdGenerator;
import com.openquartz.easytransaction.core.transaction.TransactionSupport;
import com.openquartz.easytransaction.core.trigger.TccTrigger;
import com.openquartz.easytransaction.core.trigger.TccTriggerImpl;
import com.openquartz.easytransaction.repository.api.TransactionCertificateRepository;
import com.openquartz.easytransaction.repository.jdbc.JdbcTransactionCertificateRepositoryImpl;
import com.openquartz.easytransaction.starter.aop.TccAnnotationAdvisor;
import com.openquartz.easytransaction.starter.aop.TccTryMethodInterceptor;
import com.openquartz.easytransaction.starter.spring.boot.autoconfig.properties.EasyTransactionProperties;
import com.openquartz.easytransaction.starter.spring.boot.autoconfig.properties.EasyTransactionProperties.EasyTransactionDataSourceProperties;
import com.openquartz.easytransaction.starter.transaction.SpringTransactionSupport;
import java.util.Map;
import java.util.Properties;
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
    public GlobalTransactionIdGenerator globalTransactionIdGenerator(){
        return new DefaultGlobalTransactionIdGeneratorImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionSupport transactionSupport(){
        return new SpringTransactionSupport();
    }

    @Bean
    @ConditionalOnMissingBean(type = "localStorageJdbcTemplate", value = JdbcTemplate.class)
    public JdbcTemplate jdbcTemplate(EasyTransactionProperties easyTransactionProperties,
        Environment environment,
        @Autowired(required = false)DataSource dataSource) {
        if (dataSource!=null){
            return new JdbcTemplate(dataSource);
        }
        return new JdbcTemplate(newLocalStorageDataSource(easyTransactionProperties.getDataSource(), environment));
    }

    private DataSource newLocalStorageDataSource(EasyTransactionDataSourceProperties easyFileLocalProperties,
        Environment environment) {

        Iterable<ConfigurationPropertySource> sources = ConfigurationPropertySources
            .get(environment);
        Binder binder = new Binder(sources);
        Properties properties = binder.bind(EasyTransactionProperties.PREFIX+".datasource", Properties.class).get();

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
    public TransactionCertificateRepository transactionCertificateRepository(JdbcTemplate jdbcTemplate){
        return new JdbcTransactionCertificateRepositoryImpl(jdbcTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public TccTrigger tccTrigger(TransactionSupport transactionSupport, TransactionCertificateRepository transactionCertificateRepository){
        return new TccTriggerImpl(transactionSupport,transactionCertificateRepository);
    }

    @Bean
    @Role(value = BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor fileExportExecutorAnnotationAdvisor(TccTrigger tccTrigger,
        GlobalTransactionIdGenerator globalTransactionIdGenerator,
        TransactionSupport transactionSupport,
        TransactionCertificateRepository transactionCertificateRepository,
        EasyTransactionProperties  easyTransactionProperties
    ) {
        TccTryMethodInterceptor interceptor = new TccTryMethodInterceptor(tccTrigger, globalTransactionIdGenerator,
            transactionSupport, transactionCertificateRepository);
        TccAnnotationAdvisor advisor = new TccAnnotationAdvisor(interceptor);
        advisor.setOrder(easyTransactionProperties.getTransactionAdvisorOrder());
        return advisor;
    }

}
