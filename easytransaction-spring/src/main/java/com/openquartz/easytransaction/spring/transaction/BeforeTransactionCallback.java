package com.openquartz.easytransaction.spring.transaction;

@FunctionalInterface
public interface BeforeTransactionCallback {

    /**
     * do before in transaction commit
     */
    void doBeforeCommit();

}
