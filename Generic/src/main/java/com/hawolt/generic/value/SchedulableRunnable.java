package com.hawolt.generic.value;

import com.hawolt.generic.runnable.ExceptionalRunnable;
import com.hawolt.generic.runnable.IExceptionCallback;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created: 12/01/2023 23:18
 * Author: Twitter @hawolt
 **/

public abstract class SchedulableRunnable extends ExceptionalRunnable {
    private ScheduledExecutorService service;
    private ScheduledFuture<?> future;

    public SchedulableRunnable() {
        this(null);
    }

    public SchedulableRunnable(IExceptionCallback callback) {
        super(callback);
    }

    public void schedule(int initialDelay, int period, TimeUnit timeUnit) {
        schedule(initialDelay, period, timeUnit, null);
    }

    public void schedule(int initialDelay, int period, TimeUnit timeUnit, Runnable onExecution) {
        this.service = Executors.newSingleThreadScheduledExecutor();
        this.future = service.scheduleAtFixedRate(() -> {
            run();
            if (onExecution != null) onExecution.run();
        }, initialDelay, period, timeUnit);
    }

    public void shutdown() {
        if (this.future != null) this.future.cancel(true);
        if (this.service != null) this.service.shutdown();
    }

}
