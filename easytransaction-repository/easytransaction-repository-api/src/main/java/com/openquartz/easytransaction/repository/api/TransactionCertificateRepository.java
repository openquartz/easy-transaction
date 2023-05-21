package com.openquartz.easytransaction.repository.api;

import com.openquartz.easytransaction.repository.api.model.TransactionCertificate;

public interface TransactionCertificateRepository {

    /**
     * save transaction certificate
     * @param transactionCertificate transaction certificate
     */
    void save(TransactionCertificate transactionCertificate);

    /**
     * try success
     * @param transactionCertificate transaction certificate
     */
    void trySuccess(TransactionCertificate transactionCertificate);

    /**
     * cancel transaction certificate
     * @param transactionCertificate transaction certificate
     */
    void cancel(TransactionCertificate transactionCertificate);

    /**
     * confirm transaction certificate
     * @param transactionCertificate transaction certificate
     */
    void confirm(TransactionCertificate transactionCertificate);

    /**
     * finished
     * @param transactionCertificate transaction certificate
     */
    void finished(TransactionCertificate transactionCertificate);

    /**
     * 开始重试
     * @param transactionCertificate transactionCertificate
     */
    void startRetry(TransactionCertificate transactionCertificate);
}
