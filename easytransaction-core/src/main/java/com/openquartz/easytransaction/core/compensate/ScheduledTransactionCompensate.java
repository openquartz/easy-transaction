package com.openquartz.easytransaction.core.compensate;

import com.openquartz.easytransaction.common.concurrent.TransactionThreadFactory;
import com.openquartz.easytransaction.core.compensate.property.TransactionProperties;
import com.openquartz.easytransaction.repository.api.TransactionCertificateRepository;
import com.openquartz.easytransaction.repository.api.model.TransactionCertificate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

@Slf4j
public class ScheduledTransactionCompensate implements AutoCloseable {

    private final ScheduledExecutorService tccCompensateExecutor;
    private final TransactionCertificateRepository transactionCertificateRepository;
    private final TransactionProperties transactionProperties;
    private final TransactionCompensateFactory transactionCompensateFactory;

    public ScheduledTransactionCompensate(
        TransactionCertificateRepository transactionCertificateRepository,
        TransactionProperties transactionProperties,
        TransactionCompensateFactory transactionCompensateFactory
    ) {

        this.tccCompensateExecutor = new ScheduledThreadPoolExecutor(1,
            TransactionThreadFactory.create("et-tcc-self-recovery", true));
        this.transactionCertificateRepository = transactionCertificateRepository;
        this.transactionProperties = transactionProperties;
        this.transactionCompensateFactory = transactionCompensateFactory;

        // recovery tcc
        selfTccRecovery();
    }

    private void selfTccRecovery() {
        tccCompensateExecutor
            .scheduleWithFixedDelay(() -> {
                    try {

                        Instant instant = LocalDateTime.now()
                            .plusHours(-transactionProperties.getCompensateBackOffHours())
                            .atZone(ZoneId.systemDefault())
                            .toInstant();
                        Date startCompensateTime = Date.from(instant);

                        Instant lastCompensateInstant = LocalDateTime.now()
                            .plus(-transactionProperties.getMaxTransactionTimeout(), ChronoUnit.MILLIS)
                            .atZone(ZoneId.systemDefault())
                            .toInstant();

                        Integer offset = transactionProperties.getCompensateOffset();

                        List<TransactionCertificate> transactionCertificateList =
                            transactionCertificateRepository.listCompensatedTransaction(startCompensateTime,
                                Date.from(lastCompensateInstant), offset);

                        if (CollectionUtils.isEmpty(transactionCertificateList)) {
                            return;
                        }
                        for (TransactionCertificate transactionCertificate : transactionCertificateList) {
                            transactionCompensateFactory.compensate(transactionCertificate);
                        }
                    } catch (Exception e) {
                        log.error("tcc-transaction scheduled transaction log is error:", e);
                    }
                }, transactionProperties.getCompensateInitDelay(), transactionProperties.getCompensateRecoveryDelay(),
                TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        tccCompensateExecutor.shutdown();
    }
}
