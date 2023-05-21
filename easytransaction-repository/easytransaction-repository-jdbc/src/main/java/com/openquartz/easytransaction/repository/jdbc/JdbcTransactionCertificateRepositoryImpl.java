package com.openquartz.easytransaction.repository.jdbc;

import com.openquartz.easytransaction.repository.api.TransactionCertificateRepository;
import com.openquartz.easytransaction.repository.api.model.TransactionCertificate;
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
    public void finished(TransactionCertificate transactionCertificate) {

    }

    @Override
    public void startRetry(TransactionCertificate transactionCertificate) {

    }
}
