package com.openquartz.easytransaction.core.compensate.property;

import lombok.Data;

@Data
public class TransactionProperties {

    /**
     * 事务最大超时时间
     */
    private Long maxTransactionTimeout;

    /**
     * 补偿最大重试次数
     */
    private Integer compensateRetryCount;

    /**
     * 回溯时间 单位:小时
     */
    private Integer compensateBackOffHours = 1;

    /**
     *  compensate offset
     */
    private Integer compensateOffset = 500;

    /**
     * compensate init delay
     */
    private Integer compensateInitDelay = 30;

    /**
     * compensate recovery delay
     */
    private Integer compensateRecoveryDelay = 60;
}
