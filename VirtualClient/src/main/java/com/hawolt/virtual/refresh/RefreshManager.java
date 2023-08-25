package com.hawolt.virtual.refresh;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created: 25/08/2023 15:48
 * Author: Twitter @hawolt
 **/

public class RefreshManager {
    public static ScheduledRefresh<?> submit(IRefreshable refreshable, long initialDelayInMinutes, long delayInMinutes) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> future = service.scheduleAtFixedRate(() -> {
            for (ExceptionalRefreshable task : refreshable.getRefreshableList()) {
                try {
                    task.refresh();
                } catch (Exception e) {
                    refreshable.onRefreshException(e);
                }
            }
        }, initialDelayInMinutes, delayInMinutes, TimeUnit.MINUTES);
        return new ScheduledRefresh<>(service, future);
    }
}
