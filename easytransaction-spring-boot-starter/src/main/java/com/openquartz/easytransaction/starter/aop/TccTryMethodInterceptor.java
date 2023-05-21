package com.openquartz.easytransaction.starter.aop;

import com.openquartz.easytransaction.common.exception.ExceptionUtils;
import com.openquartz.easytransaction.common.json.JSONUtil;
import com.openquartz.easytransaction.common.retry.RetryUtil;
import com.openquartz.easytransaction.repository.api.TransactionCertificateRepository;
import com.openquartz.easytransaction.repository.api.model.CertificateStatusEnum;
import com.openquartz.easytransaction.repository.api.model.TransactionCertificate;
import com.openquartz.easytransaction.core.annotation.Tcc;
import com.openquartz.easytransaction.core.generator.GlobalTransactionIdGenerator;
import com.openquartz.easytransaction.core.transaction.TransactionSupport;
import com.openquartz.easytransaction.core.trigger.TccTrigger;
import java.lang.reflect.Method;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Tcc implementation method interceptor
 *
 * @author svnee
 **/
@Slf4j
public class TccTryMethodInterceptor implements MethodInterceptor {

    private final TccTrigger tccTrigger;
    private final GlobalTransactionIdGenerator globalTransactionIdGenerator;
    private final TransactionSupport transactionSupport;
    private final TransactionCertificateRepository transactionCertificateRepository;

    public TccTryMethodInterceptor(TccTrigger tccTrigger,
        GlobalTransactionIdGenerator globalTransactionIdGenerator,
        TransactionSupport transactionSupport,
        TransactionCertificateRepository transactionCertificateRepository) {
        this.tccTrigger = tccTrigger;
        this.globalTransactionIdGenerator = globalTransactionIdGenerator;
        this.transactionSupport = transactionSupport;
        this.transactionCertificateRepository = transactionCertificateRepository;
    }

    @Override
    public Object invoke(MethodInvocation invocation) {

        // register method in local transaction
        TransactionCertificate transactionCertificate = registerTccMethodInLocalTransaction(invocation);

        Tcc annotation = invocation.getMethod().getDeclaredAnnotation(Tcc.class);
        Object tryResult = null;
        if (annotation.retryCount() > 0) {
            tryResult = RetryUtil.retry(annotation.retryCount(), annotation.retryInterval(), () -> {
                try {
                    return invocation.proceed();
                } catch (Throwable e) {
                    return ExceptionUtils.rethrow(e);
                }
            });
        } else {
            try {
                tryResult = invocation.proceed();
            } catch (Throwable e) {
                ExceptionUtils.rethrow(e);
            }
        }

        // try success
        transactionSupport.executeNewTransaction(() -> {
            transactionCertificateRepository.trySuccess(transactionCertificate);
            return true;
        });

        // do in transaction if execute success
        transactionSupport.execute(() -> {
            transactionCertificateRepository.confirm(transactionCertificate);
            return true;
        });

        return tryResult;
    }

    private TransactionCertificate registerTccMethodInLocalTransaction(MethodInvocation invocation) {
        TransactionCertificate transactionCertificate = transactionSupport.executeNewTransaction(() -> {
            // 注册Try method translation
            return registerTryTcc(invocation);
        });

        // confirm
        transactionSupport.executeAfterCommit(() -> tccTrigger.confirm(transactionCertificate));

        // cancel
        transactionSupport.executeAfterRollback(() -> tccTrigger.cancel(transactionCertificate));
        return transactionCertificate;
    }


    private TransactionCertificate registerTryTcc(MethodInvocation invocation) {

        Object[] arguments = invocation.getArguments();
        Object argument = arguments[0];
        String paramJson = JSONUtil.toClassJson(argument);

        Tcc annotation = invocation.getMethod().getDeclaredAnnotation(Tcc.class);
        String cancelMethodName = annotation.cancelMethod();
        String confirmMethodName = annotation.confirmMethod();

        Method cancelMethod = parseMethod(invocation.getThis().getClass(), cancelMethodName);
        Method confirmMethod = parseMethod(invocation.getThis().getClass(), confirmMethodName);

        // generate global transaction identifier
        String transactionId = globalTransactionIdGenerator.generateGlobalTransactionId();

        TransactionCertificate transactionCertificate = new TransactionCertificate();
        transactionCertificate.setTransactionId(transactionId);
        transactionCertificate.setCertificateStatus(CertificateStatusEnum.INIT);
        transactionCertificate.setCreatedTime(new Date());
        transactionCertificate.setUpdatedTime(new Date());
        transactionCertificate.setConfirmMethod(confirmMethod);
        transactionCertificate.setParam(paramJson);
        transactionCertificate.setCancelMethod(cancelMethod);
        transactionCertificate.setRetryCount(0);

        // save certificate
        transactionCertificateRepository.save(transactionCertificate);

        return transactionCertificate;
    }

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
