package com.openquartz.easytransaction.example.tcc.order.common;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionConfig {

    @Bean
    public Executor triggerExecutor() {
        return Executors.newCachedThreadPool();
    }
}
