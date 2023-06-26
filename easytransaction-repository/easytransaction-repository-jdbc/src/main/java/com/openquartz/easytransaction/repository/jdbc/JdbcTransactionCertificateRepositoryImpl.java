package com.openquartz.easytransaction.repository.jdbc;

import com.openquartz.easytransaction.repository.api.TransactionCertificateRepository;
import com.openquartz.easytransaction.repository.api.model.CertificateStatusEnum;
import com.openquartz.easytransaction.repository.api.model.TransactionCertificate;
import com.openquartz.easytransaction.repository.jdbc.model.TransactionCertificateEntity;
import com.openquartz.easytransaction.repository.jdbc.translator.TransactionCertificateTranslator;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * Jdbc Transaction
 *
 * @author svnee
 */
public class JdbcTransactionCertificateRepositoryImpl implements TransactionCertificateRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL =
        "insert into et_transaction_certificate_entity (transaction_id,transaction_group_id, certificate_status, created_time, finished_time, updated_time, confirm_method,`param`, cancel_method, retry_count, version)\n"
            + "values (?,?,?,?,?,?,?,?,?,?)";
    private static final String REFRESH_STATUS_SQL = "update et_transaction_certificate_entity set certificate_status=?,updated_time=now(),version = version+1 where transaction_id=? and version=?";

    private static final String FINISH_SQL = "update et_transaction_certificate_entity set certificate_status=?,updated_time=now(),finished_time=?,version = version+1 where transaction_id=? and version=?";

    private static final String START_RETRY_SQL = "update et_transaction_certificate_entity set version=version+1,updated_time=now(),retry_count=retry_count+1 where transaction_id=? and version=?";

    private static final String GET_COMPENSATE_SQL = "select id,transaction_id,transaction_group_id, certificate_status, created_time, finished_time, updated_time, confirm_method,`param`, cancel_method, retry_count, version from et_transaction_certificate_entity where certificate_status in (:certificateStatus) and updated_time >= :startTime and updatedTime < :endTime limit :offset";

    public JdbcTransactionCertificateRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(TransactionCertificate transactionCertificate) {
        TransactionCertificateEntity entity = TransactionCertificateTranslator.translate2Entity(transactionCertificate);
        save(entity);
    }

    private void save(TransactionCertificateEntity entity) {
        jdbcTemplate.update(INSERT_SQL, entity.getTransactionId(),
            entity.getTransactionGroupId(),
            entity.getCertificateStatus(),
            entity.getCreatedTime(),
            entity.getFinishedTime(),
            entity.getUpdatedTime(),
            entity.getConfirmMethod(),
            entity.getParam(),
            entity.getCancelMethod(),
            entity.getRetryCount(),
            entity.getVersion());
    }

    @Override
    public void trySuccess(TransactionCertificate transactionCertificate) {
        transactionCertificate.setCertificateStatus(CertificateStatusEnum.TRY_SUCCESS);
        updateStatus(transactionCertificate);
    }

    @Override
    public void cancel(TransactionCertificate transactionCertificate) {
        transactionCertificate.setCertificateStatus(CertificateStatusEnum.CANCEL);
        updateStatus(transactionCertificate);
    }

    private void updateStatus(TransactionCertificate transactionCertificate) {

        transactionCertificate.setUpdatedTime(new Date());

        String transactionId = transactionCertificate.getTransactionId();
        int affectedRow = jdbcTemplate.update(REFRESH_STATUS_SQL, transactionCertificate.getCertificateStatus(),
            transactionId, transactionCertificate.getVersion());
        if (affectedRow <= 0) {
            throw new RuntimeException("Failed to refresh transaction certificate status");
        }

        transactionCertificate.setVersion(transactionCertificate.getVersion() + 1);
    }

    @Override
    public void confirm(TransactionCertificate transactionCertificate) {
        transactionCertificate.setCertificateStatus(CertificateStatusEnum.CONFIRM);
        updateStatus(transactionCertificate);
    }

    @Override
    public void finish(TransactionCertificate transactionCertificate) {
        transactionCertificate.setCertificateStatus(CertificateStatusEnum.FINISHED);
        transactionCertificate.setFinishedTime(new Date());
        transactionCertificate.setUpdatedTime(new Date());

        String transactionId = transactionCertificate.getTransactionId();
        int affectedRow = jdbcTemplate.update(FINISH_SQL,
            transactionCertificate.getCertificateStatus(),
            transactionCertificate.getFinishedTime(),
            transactionId,
            transactionCertificate.getVersion());
        if (affectedRow <= 0) {
            throw new RuntimeException("Failed to finish transaction certificate status");
        }

        transactionCertificate.setVersion(transactionCertificate.getVersion() + 1);
    }

    @Override
    public boolean startRetry(TransactionCertificate transactionCertificate) {

        String transactionId = transactionCertificate.getTransactionId();
        int affectedRow = jdbcTemplate.update(START_RETRY_SQL,
            transactionId,
            transactionCertificate.getVersion());
        if (affectedRow <= 0) {
            return false;
        }

        transactionCertificate.setUpdatedTime(new Date());
        transactionCertificate.setRetryCount(transactionCertificate.getRetryCount() + 1);
        transactionCertificate.setVersion(transactionCertificate.getVersion() + 1);
        return true;
    }

    @Override
    public List<TransactionCertificate> listCompensatedTransaction(Date startRetryTime, Date lastRetryTime,
        Integer offset) {

        List<Integer> certificateStatusList = CertificateStatusEnum.getProcessingCertificateStatusList()
            .stream()
            .map(CertificateStatusEnum::getCode)
            .collect(Collectors.toList());

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("certificateStatus", certificateStatusList);
        paramMap.put("startTime", startRetryTime);
        paramMap.put("endTime", lastRetryTime);
        paramMap.put("offset", offset);

        List<TransactionCertificateEntity> entityList = new NamedParameterJdbcTemplate(jdbcTemplate)
            .query(GET_COMPENSATE_SQL, paramMap, (rs, rowNum) -> {
                TransactionCertificateEntity entity = new TransactionCertificateEntity();
                entity.setId(rs.getLong("id"));
                entity.setTransactionId(rs.getString("transaction_id"));
                entity.setTransactionGroupId(rs.getString("transaction_group_id"));
                entity.setCertificateStatus(rs.getInt("certificate_status"));
                entity.setCreatedTime(rs.getDate("created_time"));
                entity.setFinishedTime(rs.getDate("finished_time"));
                entity.setUpdatedTime(rs.getDate("updated_time"));
                entity.setConfirmMethod(rs.getString("confirm_method"));
                entity.setParam(rs.getString("param"));
                entity.setCancelMethod(rs.getString("cancel_method"));
                entity.setRetryCount(rs.getInt("retry_count"));
                entity.setVersion(rs.getInt("version"));
                return entity;
            });

        return entityList.stream().map(TransactionCertificateTranslator::translate).collect(Collectors.toList());
    }
}
