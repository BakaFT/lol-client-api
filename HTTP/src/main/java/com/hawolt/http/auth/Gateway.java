package com.hawolt.http.auth;

import java.net.Proxy;

/**
 * Created: 26/11/2022 14:19
 * Author: Twitter @hawolt
 **/

public class Gateway {
    private final Authentication authentication;
    private final Proxy proxy;

    private long lastInteraction;

    public Gateway(Proxy proxy, Authentication authentication) {
        this.authentication = authentication;
        this.proxy = proxy;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public long getLastInteraction() {
        return lastInteraction;
    }

    public void setLastInteraction(long lastInteraction) {
        this.lastInteraction = lastInteraction;
    }
}
