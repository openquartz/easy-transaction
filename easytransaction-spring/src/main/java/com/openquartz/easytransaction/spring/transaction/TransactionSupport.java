package com.openquartz.easytransaction.spring.transaction;

/**
 * Transaction Support
 *
 * @author svnee
 */
public interface TransactionSupport {

    /**
     * 执行
     *
     * @param callback 回调
     * @param <T> T
     * @return 返回结果
     */
    <T> T execute(InTransactionCallback<T> callback);

    /**
     * 开启新事务只I型那个
     * @param callback callback function
     * @return execute result obj
     * @param <T> T
     */
    <T> T executeNewTransaction(InTransactionCallback<T> callback);

    /**
     * 事务提交后回调执行
     *
     * @param callback 回调
     */
    void executeAfterCommit(AfterTransactionCallback callback);

    /**
     * 事务回滚后调用
     * @param callback callback function
     */
    void executeAfterRollback(AfterTransactionCallback callback);

    /**
     * execute before commit
     * @param callback callback
     */
    void executeBeforeCommit(BeforeTransactionCallback callback);
}
