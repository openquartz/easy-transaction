package com.openquartz.easytransaction.repository.jdbc.translator;

import com.openquartz.easytransaction.repository.api.model.CertificateStatusEnum;
import com.openquartz.easytransaction.repository.api.model.TransactionCertificate;
import com.openquartz.easytransaction.repository.jdbc.model.TransactionCertificateEntity;
import java.lang.reflect.Method;

public final class TransactionCertificateTranslator {

    private TransactionCertificateTranslator() {
    }

    public static TransactionCertificate translate(TransactionCertificateEntity entity) {
        TransactionCertificate transactionCertificate = new TransactionCertificate();
        transactionCertificate.setTransactionId(entity.getTransactionId());
        transactionCertificate.setCertificateStatus(CertificateStatusEnum.of(entity.getCertificateStatus()));
        transactionCertificate.setCreatedTime(entity.getCreatedTime());
        transactionCertificate.setFinishedTime(entity.getFinishedTime());
        transactionCertificate.setUpdatedTime(entity.getUpdatedTime());
        transactionCertificate.setConfirmMethod(parseMethod(entity.getConfirmMethod()));
        transactionCertificate.setParam(entity.getParam());
        transactionCertificate.setCancelMethod(parseMethod(entity.getCancelMethod()));
        transactionCertificate.setRetryCount(entity.getRetryCount());
        transactionCertificate.setVersion(entity.getVersion());
        return transactionCertificate;
    }

    public static TransactionCertificateEntity translate2Entity(TransactionCertificate entity) {
        TransactionCertificateEntity transactionCertificateEntity = new TransactionCertificateEntity();
        transactionCertificateEntity.setTransactionId(entity.getTransactionId());
        transactionCertificateEntity.setCertificateStatus(entity.getCertificateStatus().getCode());
        transactionCertificateEntity.setCreatedTime(entity.getCreatedTime());
        transactionCertificateEntity.setFinishedTime(entity.getFinishedTime());
        transactionCertificateEntity.setUpdatedTime(entity.getUpdatedTime());
        transactionCertificateEntity.setConfirmMethod(formatMethod(entity.getConfirmMethod()));
        transactionCertificateEntity.setParam(entity.getParam());
        transactionCertificateEntity.setCancelMethod(formatMethod(entity.getCancelMethod()));
        transactionCertificateEntity.setRetryCount(entity.getRetryCount());
        transactionCertificateEntity.setVersion(entity.getVersion());
        return transactionCertificateEntity;
    }

    private static String formatMethod(Method method) {
        if (method == null) {
            return "";
        }
        return method.getDeclaringClass().getName() + "#" + method.getName();
    }

    private static Method parseMethod(String methodName) {
        if (methodName == null || methodName.isEmpty()) {
            return null;
        }
        String[] split = methodName.split("#");
        try {
            Class<?> targetClazz = Class.forName(split[0]);
            return targetClazz.getMethod(split[1]);
        } catch (Exception ex) {
            throw new IllegalArgumentException(methodName);
        }
    }
}
