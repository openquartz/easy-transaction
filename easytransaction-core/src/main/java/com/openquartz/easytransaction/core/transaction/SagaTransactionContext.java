package com.openquartz.easytransaction.core.transaction;

import com.openquartz.easytransaction.common.lang.Pair;
import com.openquartz.easytransaction.core.generator.GlobalTransactionIdGenerator;
import org.springframework.core.NamedThreadLocal;

/**
 * saga transaction context
 *
 * @author svnee
 */
public final class SagaTransactionContext {

    private SagaTransactionContext() {
    }

    private static final ThreadLocal<String> currentSagaTransactionGroup = new NamedThreadLocal<>(
        "Current Saga transaction group");

    /**
     * current saga transaction group
     *
     * @param generator the global transaction generator
     * @return the current saga transaction group
     */
    public static Pair<String, Boolean> currentSagaTransactionGroupId(GlobalTransactionIdGenerator generator) {
        String transactionGroupId = currentSagaTransactionGroup.get();
        boolean absent = false;
        if (transactionGroupId == null) {
            currentSagaTransactionGroup.set(generator.generateGlobalTransactionId());
            absent = true;
        }
        return Pair.of(currentSagaTransactionGroup.get(), absent);
    }


    /**
     * clear the current saga transaction group
     */
    public static void clear() {
        currentSagaTransactionGroup.remove();
    }

}
