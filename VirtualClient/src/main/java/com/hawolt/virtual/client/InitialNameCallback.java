package com.hawolt.virtual.client;

import com.hawolt.virtual.leagueclient.instance.AbstractVirtualLeagueClientInstance;

/**
 * Created: 03/10/2023 20:31
 * Author: Twitter @hawolt
 **/

public interface InitialNameCallback {
    InitialNameCallback BLANK = (AbstractVirtualLeagueClientInstance abstractVirtualLeagueClientInstance) -> {
        throw new RuntimeException("Please manually implement InitialNameCallback or set ignoreSummoner to true");
    };

    String getInitialName(AbstractVirtualLeagueClientInstance instance);
}
