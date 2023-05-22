package com.openquartz.easytransaction.core.compensate;

import com.openquartz.easytransaction.repository.api.TransactionCertificateRepository;
import com.openquartz.easytransaction.repository.api.model.CertificateStatusEnum;
import com.openquartz.easytransaction.repository.api.model.TransactionCertificate;
import com.openquartz.easytransaction.core.compensate.property.TransactionProperties;
import com.openquartz.easytransaction.core.trigger.TccTriggerEngine;

/**
 * Compensate implementation
 *
 * @author svnee
 */
public class TransactionCompensateFactoryImpl implements TransactionCompensateFactory {

    private final TccTriggerEngine tccTriggerEngine;
    private final TransactionProperties transactionProperties;
    private final TransactionCertificateRepository transactionCertificateRepository;

    public TransactionCompensateFactoryImpl(TccTriggerEngine tccTriggerEngine, TransactionProperties transactionProperties,
        TransactionCertificateRepository transactionCertificateRepository) {
        this.tccTriggerEngine = tccTriggerEngine;
        this.transactionProperties = transactionProperties;
        this.transactionCertificateRepository = transactionCertificateRepository;
    }

    @Override
    public void compensate(TransactionCertificate transactionCertificate) {

        // has finished
        if (transactionCertificate.isFinished()
            || transactionCertificate.getRetryCount() >= transactionProperties.getCompensateRetryCount()) {
            return;
        }

        // 未超时不做补偿
        if (System.currentTimeMillis() - transactionCertificate.getUpdatedTime().getTime()
            <= transactionProperties.getMaxTransactionTimeout()) {
            return;
        }

        // confirm transaction or cancel
        boolean retry = transactionCertificateRepository.startRetry(transactionCertificate);
        if (!retry) {
            return;
        }
        if (transactionCertificate.getCertificateStatus() == CertificateStatusEnum.CONFIRM) {
            tccTriggerEngine.confirm(transactionCertificate);
        } else {
            tccTriggerEngine.cancel(transactionCertificate);
        }
    }
}
