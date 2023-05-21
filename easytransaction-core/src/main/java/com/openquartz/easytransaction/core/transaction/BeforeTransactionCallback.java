package com.openquartz.easytransaction.core.transaction;

@FunctionalInterface
public interface BeforeTransactionCallback {

    /**
     * do before in transaction commit
     */
    void doBeforeCommit();

}
