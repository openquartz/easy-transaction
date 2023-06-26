package com.openquartz.easytransaction.repository.jdbc.translator;

import com.openquartz.easytransaction.common.json.JSONUtil;
import com.openquartz.easytransaction.common.lang.StringUtils;
import com.openquartz.easytransaction.repository.api.model.CertificateStatusEnum;
import com.openquartz.easytransaction.repository.api.model.TransactionCertificate;
import com.openquartz.easytransaction.repository.jdbc.model.TransactionCertificateEntity;
import java.lang.reflect.Method;

/**
 * translator
 *
 * @author svnee
 */
public final class TransactionCertificateTranslator {

    private static final String METHOD_SPLITTER = "#";

    private TransactionCertificateTranslator() {
    }

    public static TransactionCertificate translate(TransactionCertificateEntity entity) {
        TransactionCertificate transactionCertificate = new TransactionCertificate();
        transactionCertificate.setTransactionId(entity.getTransactionId());
        transactionCertificate.setTransactionGroupId(entity.getTransactionGroupId());
        transactionCertificate.setCertificateStatus(CertificateStatusEnum.of(entity.getCertificateStatus()));
        transactionCertificate.setCreatedTime(entity.getCreatedTime());
        transactionCertificate.setFinishedTime(entity.getFinishedTime());
        transactionCertificate.setUpdatedTime(entity.getUpdatedTime());
        transactionCertificate.setConfirmMethod(parseMethod(entity.getConfirmMethod()));
        transactionCertificate.setParam(parseParam(entity.getParam()));
        transactionCertificate.setCancelMethod(parseMethod(entity.getCancelMethod()));
        transactionCertificate.setRetryCount(entity.getRetryCount());
        transactionCertificate.setVersion(entity.getVersion());
        return transactionCertificate;
    }

    private static Object parseParam(String paramJson) {
        if (paramJson == null || paramJson.isEmpty()) {
            return null;
        }
        return JSONUtil.parseObject(paramJson, Object.class);
    }

    public static TransactionCertificateEntity translate2Entity(TransactionCertificate entity) {
        TransactionCertificateEntity transactionCertificateEntity = new TransactionCertificateEntity();
        transactionCertificateEntity.setTransactionId(entity.getTransactionId());
        transactionCertificateEntity.setTransactionGroupId(entity.getTransactionGroupId());
        transactionCertificateEntity.setCertificateStatus(entity.getCertificateStatus().getCode());
        transactionCertificateEntity.setCreatedTime(entity.getCreatedTime());
        transactionCertificateEntity.setFinishedTime(entity.getFinishedTime());
        transactionCertificateEntity.setUpdatedTime(entity.getUpdatedTime());
        transactionCertificateEntity.setConfirmMethod(formatMethod(entity.getConfirmMethod()));
        transactionCertificateEntity.setParam(JSONUtil.toClassJson(entity.getParam()));
        transactionCertificateEntity.setCancelMethod(formatMethod(entity.getCancelMethod()));
        transactionCertificateEntity.setRetryCount(entity.getRetryCount());
        transactionCertificateEntity.setVersion(entity.getVersion());
        return transactionCertificateEntity;
    }

    /**
     * format method
     *
     * @param method method
     * @return method signature
     */
    private static String formatMethod(Method method) {
        if (method == null) {
            return StringUtils.EMPTY;
        }
        return method.getDeclaringClass().getName() + METHOD_SPLITTER + method.getName();
    }

    /**
     * parse method
     *
     * @param methodName method name
     * @return method instance
     */
    private static Method parseMethod(String methodName) {
        if (methodName == null || methodName.isEmpty()) {
            return null;
        }
        String[] split = methodName.split(METHOD_SPLITTER);
        try {
            Class<?> targetClazz = Class.forName(split[0]);
            return targetClazz.getMethod(split[1]);
        } catch (Exception ex) {
            throw new IllegalArgumentException(methodName);
        }
    }
}
