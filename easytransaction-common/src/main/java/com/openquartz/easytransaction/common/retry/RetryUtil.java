package com.openquartz.easytransaction.common.retry;

import com.openquartz.easytransaction.common.exception.ExceptionUtils;
import java.util.function.Supplier;

/**
 * 重试Utils
 *
 * @author svnee
 */
public class RetryUtil {

    /**
     * 重试调用
     *
     * @param numRetries 重试次水
     * @param sleepMillis 调用间隔
     * @param supplier 重试方法
     * @param <T> T
     * @return 结果
     */
    public static <T> T retry(int numRetries, long sleepMillis, Supplier<T> supplier) {

        for (int i = 0; i < numRetries; i++) {
            try {
                return supplier.get();
            } catch (Exception e) {
                if (i == numRetries - 1) {
                    throw e;
                }
                try {
                    Thread.sleep(sleepMillis);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return ExceptionUtils.rethrow(ex);
                }
            }
        }
        return null;
    }
}