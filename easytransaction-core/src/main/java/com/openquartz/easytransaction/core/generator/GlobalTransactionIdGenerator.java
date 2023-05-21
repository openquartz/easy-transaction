package com.openquartz.easytransaction.core.generator;

public interface GlobalTransactionIdGenerator {

    /**
     * 生成全局事务ID
     *
     * @return 全局事务ID
     */
    String generateGlobalTransactionId();

}
