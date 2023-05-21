package com.openquartz.easytransaction.spring.trigger;

import com.openquartz.easytransaction.repository.api.TransactionCertificateRepository;
import com.openquartz.easytransaction.repository.api.model.TransactionCertificate;
import com.openquartz.easytransaction.spring.transaction.TransactionSupport;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TccTriggerImpl implements TccTrigger {

    private final TransactionSupport transactionSupport;
    private final TransactionCertificateRepository transactionCertificateRepository;

    public TccTriggerImpl(TransactionSupport transactionSupport,
        TransactionCertificateRepository transactionCertificateRepository) {
        this.transactionSupport = transactionSupport;
        this.transactionCertificateRepository = transactionCertificateRepository;
    }

    /**
     * cancel transaction
     *
     * @param transactionCertificate transaction certificate
     */
    public void cancel(TransactionCertificate transactionCertificate) {

        if (Objects.isNull(transactionCertificate.getCancelMethod())) {
            transactionSupport.executeNewTransaction(() -> {
                transactionCertificateRepository.finished(transactionCertificate);
                return true;
            });
            return;
        }

        // cancel
        transactionSupport.executeNewTransaction(() -> {
            transactionCertificateRepository.cancel(transactionCertificate);
            return true;
        });

        try {
            transactionCertificate.getCancelMethod().invoke(transactionCertificate.getParam());

            // finished
            transactionSupport.executeNewTransaction(() -> {
                transactionCertificateRepository.finished(transactionCertificate);
                return true;
            });
        } catch (Throwable ex) {
            log.error("[TccTryMethodInterceptor#cancel] Failed to cancel! transactionId:{}",
                transactionCertificate.getTransactionId(), ex);
        }

    }

    /**
     * confirm transaction
     *
     * @param transactionCertificate certificate
     */
    public void confirm(TransactionCertificate transactionCertificate) {

        if (Objects.isNull(transactionCertificate.getConfirmMethod())) {
            transactionSupport.executeNewTransaction(() -> {
                transactionCertificateRepository.finished(transactionCertificate);
                return true;
            });
            return;
        }

        try {
            transactionCertificate.getConfirmMethod().invoke(transactionCertificate.getParam());

            // finished
            transactionSupport.executeNewTransaction(() -> {
                transactionCertificateRepository.finished(transactionCertificate);
                return true;
            });

        } catch (Throwable ex) {
            log.error("[TccTryMethodInterceptor#confirm] Failed to confirm! transactionId:{}",
                transactionCertificate.getTransactionId(), ex);
        }
    }

}
