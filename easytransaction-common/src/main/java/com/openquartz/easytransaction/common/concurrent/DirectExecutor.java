package com.openquartz.easytransaction.common.concurrent;

import java.util.concurrent.Executor;
import org.springframework.lang.NonNull;

/**
 * DirectExecutor
 *
 * @author svnee
 */
public class DirectExecutor implements Executor {

    @Override
    public void execute(@NonNull Runnable command) {
        command.run();
    }
}
