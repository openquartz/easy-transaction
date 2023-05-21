package com.openquartz.easytransaction.core.transaction;

/**
 * TransactionAfterCommit
 *
 * @author svnee
 */
@FunctionalInterface
public interface AfterTransactionCallback {

    /**
     * 事务内執行
     */
    void doAfterCommit();

}
