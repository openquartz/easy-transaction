package com.openquartz.easytransaction.core.compensate;

import com.openquartz.easytransaction.repository.api.model.TransactionCertificate;

/**
 * 事务补偿
 */
public interface TransactionCompensateFactory {

    /**
     * 补偿
     *
     * @param transactionCertificate 事务凭证
     */
    void compensate(TransactionCertificate transactionCertificate);

}
