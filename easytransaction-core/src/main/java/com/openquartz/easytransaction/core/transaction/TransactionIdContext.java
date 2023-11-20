package com.openquartz.easytransaction.core.transaction;

import org.springframework.core.NamedThreadLocal;

/**
 * TransactionId Context
 *
 * @author svnee
 */
public final class TransactionIdContext {

    private TransactionIdContext() {
    }

    private static final ThreadLocal<String> currentTransactionId =
            new NamedThreadLocal<>("Current transaction Id");

    /**
     * current transaction id
     *
     * @return current transaction id
     */
    public static String getCurrentTransactionId() {
        return currentTransactionId.get();
    }

    /**
     * set current transaction id
     *
     * @param transactionId current transaction id
     */
    public static void putCurrentTransactionId(String transactionId) {
        currentTransactionId.set(transactionId);
    }

    /**
     * clear the current transactionId
     */
    public static void clear() {
        currentTransactionId.remove();
    }
}
