package com.openquartz.easytransaction.core.compensate;

import com.openquartz.easytransaction.repository.api.TransactionCertificateRepository;
import com.openquartz.easytransaction.repository.api.model.CertificateStatusEnum;
import com.openquartz.easytransaction.repository.api.model.TransactionCertificate;
import com.openquartz.easytransaction.core.compensate.property.TransactionProperties;
import com.openquartz.easytransaction.core.trigger.TccTrigger;

/**
 * Compensate implementation
 *
 * @author svnee
 */
public class TransactionCompensateImpl implements TransactionCompensate {

    private final TccTrigger tccTrigger;
    private final TransactionProperties transactionProperties;
    private final TransactionCertificateRepository transactionCertificateRepository;

    public TransactionCompensateImpl(TccTrigger tccTrigger, TransactionProperties transactionProperties,
        TransactionCertificateRepository transactionCertificateRepository) {
        this.tccTrigger = tccTrigger;
        this.transactionProperties = transactionProperties;
        this.transactionCertificateRepository = transactionCertificateRepository;
    }

    @Override
    public void compensate(TransactionCertificate transactionCertificate) {

        // has finished
        if (transactionCertificate.isFinished()
            || transactionCertificate.getRetryCount() >= transactionProperties.getMaxTransactionRetry()) {
            return;
        }

        // 未超时不做补偿
        if (System.currentTimeMillis() - transactionCertificate.getUpdatedTime().getTime()
            <= transactionProperties.getMaxTransactionTimeout()) {
            return;
        }

        // confirm transaction or cancel
        transactionCertificateRepository.startRetry(transactionCertificate);
        if (transactionCertificate.getCertificateStatus() == CertificateStatusEnum.CONFIRM) {
            tccTrigger.confirm(transactionCertificate);
        } else {
            tccTrigger.cancel(transactionCertificate);
        }
    }
}
