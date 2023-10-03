package com.hawolt.virtual.client;

import com.hawolt.virtual.leagueclient.client.VirtualLeagueClient;

/**
 * Created: 03/10/2023 20:31
 * Author: Twitter @hawolt
 **/

public interface InitialNameSupplier {
    InitialNameSupplier BLANK = (VirtualLeagueClient abstractVirtualLeagueClientInstance) -> {
        throw new RuntimeException("Please manually implement InitialNameCallback or set ignoreSummoner to true");
    };

    String getInitialName(VirtualLeagueClient instance);
}
