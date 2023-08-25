package com.hawolt.virtual.refresh;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * Created: 13/01/2023 18:52
 * Author: Twitter @hawolt
 **/

public record ScheduledRefresh<T>(ScheduledExecutorService service, ScheduledFuture<T> future) {

    public void stop() {
        future.cancel(true);
        service.shutdown();
    }
}
