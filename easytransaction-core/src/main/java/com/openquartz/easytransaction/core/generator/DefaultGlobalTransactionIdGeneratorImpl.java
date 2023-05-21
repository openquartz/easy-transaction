package com.openquartz.easytransaction.core.generator;

import java.util.UUID;

public class DefaultGlobalTransactionIdGeneratorImpl implements GlobalTransactionIdGenerator {

    @Override
    public String generateGlobalTransactionId() {
        return UUID.randomUUID().toString().replace("-","");
    }
}
