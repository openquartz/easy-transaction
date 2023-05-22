package com.openquartz.easytransaction.repository.api;

import com.openquartz.easytransaction.repository.api.model.TransactionCertificate;
import java.util.Date;
import java.util.List;

public interface TransactionCertificateRepository {

    /**
     * save transaction certificate
     *
     * @param transactionCertificate transaction certificate
     */
    void save(TransactionCertificate transactionCertificate);

    /**
     * try success
     *
     * @param transactionCertificate transaction certificate
     */
    void trySuccess(TransactionCertificate transactionCertificate);

    /**
     * cancel transaction certificate
     *
     * @param transactionCertificate transaction certificate
     */
    void cancel(TransactionCertificate transactionCertificate);

    /**
     * confirm transaction certificate
     *
     * @param transactionCertificate transaction certificate
     */
    void confirm(TransactionCertificate transactionCertificate);

    /**
     * finish
     *
     * @param transactionCertificate transaction certificate
     */
    void finish(TransactionCertificate transactionCertificate);

    /**
     * 开始重试
     *
     * @param transactionCertificate transactionCertificate
     */
    boolean startRetry(TransactionCertificate transactionCertificate);

    /**
     * need to compensate exception transaction
     *
     * @param startRetryTime 开始时间
     * @param lastRetryTime 最大的可重试时间
     * @param offset offset
     * @return transaction certificate
     */
    List<TransactionCertificate> listCompensatedTransaction(Date startRetryTime, Date lastRetryTime, Integer offset);
}
