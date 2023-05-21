package com.openquartz.easytransaction.repository.jdbc;

import com.openquartz.easytransaction.repository.api.TransactionCertificateRepository;
import com.openquartz.easytransaction.repository.api.model.TransactionCertificate;
import com.openquartz.easytransaction.repository.jdbc.model.TransactionCertificateEntity;
import com.openquartz.easytransaction.repository.jdbc.translator.TransactionCertificateTranslator;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Jdbc Transaction
 *
 * @author svnee
 */
public class JdbcTransactionCertificateRepositoryImpl implements TransactionCertificateRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransactionCertificateRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(TransactionCertificate transactionCertificate) {
        TransactionCertificateEntity entity = TransactionCertificateTranslator.translate2Entity(transactionCertificate);

        save(entity);
    }

    private void save(TransactionCertificateEntity transactionCertificateEntity) {

    }

    @Override
    public void trySuccess(TransactionCertificate transactionCertificate) {

    }

    @Override
    public void cancel(TransactionCertificate transactionCertificate) {

    }

    @Override
    public void confirm(TransactionCertificate transactionCertificate) {

    }

    @Override
    public void finish(TransactionCertificate transactionCertificate) {

    }

    @Override
    public void startRetry(TransactionCertificate transactionCertificate) {

    }

    @Override
    public List<TransactionCertificate> listCompensatedTransaction(Date startRetryTime, Date lastRetryTime,
        Integer offset) {

        return Collections.emptyList();
    }
}
