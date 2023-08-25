package com.hawolt.virtual.clientconfig.impl;

import com.hawolt.generic.data.Platform;
import com.hawolt.http.auth.Gateway;
import com.hawolt.virtual.clientconfig.ClientConfig;
import com.hawolt.virtual.clientconfig.impl.redge.RedgeConfig;
import okhttp3.Request;

import java.io.IOException;

/**
 * Created: 18/08/2023 17:11
 * Author: Twitter @hawolt
 **/

public class PublicClientConfig extends ClientConfig {
    private final RedgeConfig redgeConfig;

    public PublicClientConfig(Gateway gateway, Platform platform) throws IOException {
        super(gateway, platform);
        this.load();
        this.redgeConfig = new RedgeConfig(cache);
    }

    public RedgeConfig getRedgeConfig() {
        return redgeConfig;
    }

    @Override
    protected Request request() {
        return new Request.Builder()
                .url(getURL())
                .build();
    }

    @Override
    protected String getType() {
        return "public";
    }
}
