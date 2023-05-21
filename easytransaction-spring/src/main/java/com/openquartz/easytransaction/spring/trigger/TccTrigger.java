package com.openquartz.easytransaction.spring.trigger;

import com.openquartz.easytransaction.repository.api.model.TransactionCertificate;

public interface TccTrigger {

    /**
     * cancel
     * @param transactionCertificate the transaction certificate
     */
    void cancel(TransactionCertificate transactionCertificate);

    /**
     * confirm
     * @param transactionCertificate transaction certificate
     */
    void confirm(TransactionCertificate transactionCertificate);
}
