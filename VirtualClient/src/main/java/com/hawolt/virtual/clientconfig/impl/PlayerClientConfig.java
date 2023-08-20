package com.hawolt.virtual.clientconfig.impl;

import com.hawolt.generic.data.Platform;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.auth.Gateway;
import com.hawolt.virtual.clientconfig.ClientConfig;
import okhttp3.Request;

import java.io.IOException;

/**
 * Created: 18/08/2023 17:11
 * Author: Twitter @hawolt
 **/

public class PlayerClientConfig extends ClientConfig {
    private final StringTokenSupplier supplier;

    public PlayerClientConfig(Gateway gateway, Platform platform, StringTokenSupplier supplier) throws IOException {
        super(gateway, platform);
        this.supplier = supplier;
    }

    @Override
    protected Request request() {
        return new Request.Builder()
                .url(getURL())
                .header("Authorization",
                        String.join(
                                " ",
                                "Bearer",
                                supplier.get("clientconfig.riot-client.access_token", true)
                        )
                )
                .header(
                        "X-Riot-RSO-Identity-JWT",
                        supplier.get("clientconfig.riot-client.id_token", true)
                )
                .header(
                        "X-Riot-Entitlements-JWT",
                        supplier.get("clientconfig.entitlement.riot-client.entitlements_token", true)
                )
                .build();
    }

    @Override
    protected String getType() {
        return "player";
    }
}
