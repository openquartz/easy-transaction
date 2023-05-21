package com.openquartz.easytransaction.spring.compensate;

import com.openquartz.easytransaction.repository.api.model.TransactionCertificate;

/**
 * 事务补偿
 */
public interface TransactionCompensate {

    /**
     * 补偿
     *
     * @param transactionCertificate 事务凭证
     */
    void compensate(TransactionCertificate transactionCertificate);

}
