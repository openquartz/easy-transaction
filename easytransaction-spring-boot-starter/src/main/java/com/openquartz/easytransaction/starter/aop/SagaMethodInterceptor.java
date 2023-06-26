package com.openquartz.easytransaction.starter.aop;

import com.openquartz.easytransaction.common.exception.ExceptionUtils;
import com.openquartz.easytransaction.common.json.JSONUtil;
import com.openquartz.easytransaction.common.lang.Pair;
import com.openquartz.easytransaction.common.retry.RetryUtil;
import com.openquartz.easytransaction.core.annotation.Saga;
import com.openquartz.easytransaction.core.generator.GlobalTransactionIdGenerator;
import com.openquartz.easytransaction.core.transaction.SagaTransactionContext;
import com.openquartz.easytransaction.core.transaction.TransactionSupport;
import com.openquartz.easytransaction.core.trigger.TccTriggerEngine;
import com.openquartz.easytransaction.repository.api.TransactionCertificateRepository;
import com.openquartz.easytransaction.repository.api.model.CertificateStatusEnum;
import com.openquartz.easytransaction.repository.api.model.TransactionCertificate;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Saga Method Transaction Interceptor
 *
 * @author svnee
 */
public class SagaMethodInterceptor implements MethodInterceptor {

    private final TccTriggerEngine tccTriggerEngine;
    private final GlobalTransactionIdGenerator globalTransactionIdGenerator;
    private final TransactionSupport transactionSupport;
    private final TransactionCertificateRepository transactionCertificateRepository;

    public SagaMethodInterceptor(TccTriggerEngine tccTriggerEngine,
        GlobalTransactionIdGenerator globalTransactionIdGenerator,
        TransactionSupport transactionSupport,
        TransactionCertificateRepository transactionCertificateRepository) {
        this.tccTriggerEngine = tccTriggerEngine;
        this.globalTransactionIdGenerator = globalTransactionIdGenerator;
        this.transactionSupport = transactionSupport;
        this.transactionCertificateRepository = transactionCertificateRepository;
    }

    @Override
    public Object invoke(MethodInvocation invocation) {

        // register method in local transaction
        Pair<String, Boolean> sagaTransactionGroupPair = SagaTransactionContext
            .currentSagaTransactionGroupId(globalTransactionIdGenerator);
        try {
            TransactionCertificate transactionCertificate =
                registerSagaMethodInLocalTransaction(invocation, sagaTransactionGroupPair.getK());

            Saga annotation = invocation.getMethod().getDeclaredAnnotation(Saga.class);
            Object tryResult = executeConfirmMethod(invocation, annotation);

            // confirm success
            transactionSupport.executeNewTransaction(() -> {
                transactionCertificateRepository.confirm(transactionCertificate);
                return true;
            });

            return tryResult;
        } finally {
            if (Boolean.TRUE.equals(sagaTransactionGroupPair.getV())) {
                SagaTransactionContext.clear();
            }
        }
    }

    private Object executeConfirmMethod(MethodInvocation invocation, Saga annotation) {

        // 执行次数
        int retryCount = (annotation.retryCount() > 0) ? annotation.retryCount() + 1 : 1;

        try {
            // future get
            FutureTask<Object> future = new FutureTask<>(
                // retry invoke try method
                () -> RetryUtil.retry(retryCount, annotation.retryInterval(), () -> {
                    try {
                        return invocation.proceed();
                    } catch (Throwable e) {
                        return ExceptionUtils.rethrow(e);
                    }
                }));
            return future.get(annotation.timeout(), TimeUnit.MICROSECONDS);
        } catch (TimeoutException | ExecutionException e) {
            return ExceptionUtils.rethrow(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ExceptionUtils.rethrow(e);
        }
    }

    /**
     * register TccMethod in local transaction
     *
     * @param invocation invocation
     * @return transaction certificate
     */
    private TransactionCertificate registerSagaMethodInLocalTransaction(MethodInvocation invocation,
        String sagaTransactionGroup) {

        TransactionCertificate transactionCertificate = transactionSupport.executeNewTransaction(() -> {
            // 注册Try method transaction
            return registerTryTcc(invocation, sagaTransactionGroup);
        });

        // rollback method
        transactionSupport.executeAfterRollback(() -> tccTriggerEngine.cancel(transactionCertificate));
        return transactionCertificate;
    }


    private TransactionCertificate registerTryTcc(MethodInvocation invocation, String sagaTransactionGroup) {

        Object[] arguments = invocation.getArguments();
        Object argument = arguments[0];
        String paramJson = JSONUtil.toClassJson(argument);

        Saga annotation = invocation.getMethod().getDeclaredAnnotation(Saga.class);
        String cancelMethodName = annotation.rollbackMethod();

        Method cancelMethod = parseMethod(invocation.getThis().getClass(), cancelMethodName);

        // generate global transaction identifier
        String transactionId = globalTransactionIdGenerator.generateGlobalTransactionId();

        TransactionCertificate transactionCertificate = new TransactionCertificate();
        transactionCertificate.setTransactionId(transactionId);
        transactionCertificate.setTransactionGroupId(sagaTransactionGroup);
        transactionCertificate.setCertificateStatus(CertificateStatusEnum.INIT);
        transactionCertificate.setCreatedTime(new Date());
        transactionCertificate.setUpdatedTime(new Date());
        transactionCertificate.setConfirmMethod(null);
        transactionCertificate.setParam(paramJson);
        transactionCertificate.setCancelMethod(cancelMethod);
        transactionCertificate.setRetryCount(0);

        // save certificate
        transactionCertificateRepository.save(transactionCertificate);

        return transactionCertificate;
    }

    /**
     * parse transaction method
     *
     * @param targetClass target class
     * @param methodName method name
     * @return method
     */
    private static Method parseMethod(Class<?> targetClass, String methodName) {
        if (methodName != null && !methodName.isEmpty()) {
            try {
                return targetClass.getMethod(methodName);
            } catch (Throwable ex) {
                return ExceptionUtils.rethrow(ex);
            }
        }
        return null;
    }

}
