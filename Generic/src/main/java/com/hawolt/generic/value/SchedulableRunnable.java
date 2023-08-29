package com.hawolt.generic.value;

import com.hawolt.generic.runnable.ExceptionalRunnable;
import com.hawolt.generic.runnable.IExceptionCallback;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
        schedule(initialDelay, period, timeUnit, null, null);
    }

    public void schedule(int initialDelay, int period, TimeUnit timeUnit, Runnable post, Consumer<Exception> consumer) {
        this.service = Executors.newSingleThreadScheduledExecutor();
        this.future = service.scheduleAtFixedRate(() -> {
            try {
                run();
                if (post != null) post.run();
            } catch (Exception e) {
                if (consumer != null) consumer.accept(e);
            }
        }, initialDelay, period, timeUnit);
    }

    public void shutdown() {
        if (this.future != null) this.future.cancel(true);
        if (this.service != null) this.service.shutdown();
    }

}
