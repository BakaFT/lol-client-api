package com.hawolt.virtual.leagueclient.refresh;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * Created: 13/01/2023 18:52
 * Author: Twitter @hawolt
 **/

public class ScheduledRefresh<T> {
    private final ScheduledExecutorService service;
    private final ScheduledFuture<T> future;

    public ScheduledRefresh(ScheduledExecutorService service, ScheduledFuture<T> future) {
        this.service = service;
        this.future = future;
    }

    public ScheduledExecutorService getService() {
        return service;
    }

    public ScheduledFuture<T> getFuture() {
        return future;
    }

    public void stop() {
        future.cancel(true);
        service.shutdown();
    }
}
