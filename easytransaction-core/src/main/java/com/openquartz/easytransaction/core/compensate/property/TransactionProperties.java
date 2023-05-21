package com.openquartz.easytransaction.core.compensate.property;

import lombok.Data;

@Data
public class TransactionProperties {

    /**
     * 事务最大超时时间
     */
    private Long maxTransactionTimeout;

    /**
     * 最大重试次数
     */
    private Integer maxTransactionRetry;

}
