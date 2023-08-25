package com.hawolt.virtual.refresh;

import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.auth.Gateway;
import com.hawolt.logger.Logger;
import com.hawolt.virtual.leagueclient.authentication.RefreshableTokenSetup;

import java.util.Collections;
import java.util.List;

/**
 * Created: 25/08/2023 21:31
 * Author: Twitter @hawolt
 **/

public class RefreshTask implements IRefreshable {
    private final StringTokenSupplier tokenSupplier;
    private final RefreshableTokenSetup refreshable;
    private final String userAgent;
    private final Gateway gateway;

    public RefreshTask(RefreshableTokenSetup refreshable, Gateway gateway, String userAgent, StringTokenSupplier tokenSupplier) {
        this.tokenSupplier = tokenSupplier;
        this.refreshable = refreshable;
        this.userAgent = userAgent;
        this.gateway = gateway;
    }

    @Override
    public List<ExceptionalRefreshable> getRefreshableList() {
        return Collections.singletonList(() -> refreshable.refresh(gateway, userAgent, tokenSupplier));
    }

    @Override
    public void onRefreshException(Throwable throwable) {
        Logger.error(throwable);
    }

    @Override
    public void refresh() {
        for (ExceptionalRefreshable refreshable : getRefreshableList()) {
            try {
                refreshable.refresh();
            } catch (Exception e) {
                onRefreshException(e);
            }
        }
    }
}
