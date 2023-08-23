package com.hawolt.virtual.clientconfig.impl;

import com.hawolt.generic.data.Platform;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.auth.Gateway;
import com.hawolt.virtual.clientconfig.ClientConfig;
import com.hawolt.virtual.clientconfig.impl.rms.RiotMessageServiceConfig;
import okhttp3.Request;

import java.io.IOException;

/**
 * Created: 18/08/2023 17:11
 * Author: Twitter @hawolt
 **/

public class PlayerClientConfig extends ClientConfig {
    private final RiotMessageServiceConfig riotMessageServiceConfig;
    private final StringTokenSupplier supplier;

    public PlayerClientConfig(Gateway gateway, Platform platform, StringTokenSupplier supplier) throws IOException {
        super(gateway, platform);
        this.supplier = supplier;
        this.load();
        this.riotMessageServiceConfig = new RiotMessageServiceConfig(cache);
    }

    public RiotMessageServiceConfig getRiotMessageServiceConfig() {
        return riotMessageServiceConfig;
    }

    @Override
    protected Request request() {
        return new Request.Builder()
                .url(getURL())
                .header("Authorization",
                        String.join(
                                " ",
                                "Bearer",
                                supplier.getSimple("access_token")
                        )
                )
                .header(
                        "X-Riot-RSO-Identity-JWT",
                        supplier.getSimple("id_token")
                )
                .header(
                        "X-Riot-Entitlements-JWT",
                        supplier.getSimple("entitlements_token")
                )
                .build();
    }

    @Override
    protected String getType() {
        return "player";
    }
}
