package com.openquartz.easytransaction.spring.transaction;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class SpringTransactionSupport implements TransactionSupport {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <T> T execute(InTransactionCallback<T> callback) {
        return callback.doInTransaction();
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public <T> T executeNewTransaction(InTransactionCallback<T> callback) {
        return callback.doInTransaction();
    }

    @Override
    public void executeAfterCommit(AfterTransactionCallback callback) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            callback.doAfterCommit();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                callback.doAfterCommit();
            }
        });
    }

    @Override
    public void executeAfterRollback(AfterTransactionCallback callback) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            callback.doAfterCommit();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                    callback.doAfterCommit();
                }
            }
        });
    }

    @Override
    public void executeBeforeCommit(BeforeTransactionCallback callback) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            callback.doBeforeCommit();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void beforeCommit(boolean readOnly) {
                callback.doBeforeCommit();
            }
        });
    }
}
