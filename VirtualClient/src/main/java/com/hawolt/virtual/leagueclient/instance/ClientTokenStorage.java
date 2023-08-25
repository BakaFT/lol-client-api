package com.hawolt.virtual.leagueclient.instance;

import com.hawolt.authentication.Authorization;
import com.hawolt.authentication.ClientID;
import com.hawolt.authentication.ClientScope;
import com.hawolt.authentication.ResponseType;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.auth.Gateway;
import com.hawolt.logger.Logger;
import com.hawolt.virtual.leagueclient.authentication.impl.Entitlement;
import com.hawolt.virtual.leagueclient.authentication.impl.Userinfo;
import com.hawolt.virtual.misc.Authorizable;
import com.hawolt.virtual.refresh.ExceptionalRefreshable;
import com.hawolt.virtual.refresh.IRefreshable;
import com.hawolt.virtual.riotclient.instance.IVirtualRiotClientInstance;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created: 25/08/2023 22:36
 * Author: Twitter @hawolt
 **/

public class ClientTokenStorage implements Authorizable, IRefreshable {
    private final IVirtualLeagueClientInstance virtualLeagueClientInstance;
    private final IVirtualRiotClientInstance virtualRiotClientInstance;
    private final Map<Integer, Entitlement> map = new HashMap<>();
    private final Userinfo userinfo;
    private final Gateway gateway;

    private StringTokenSupplier tokenSupplier;

    public ClientTokenStorage(IVirtualLeagueClientInstance virtualLeagueClientInstance) {
        this.virtualRiotClientInstance = virtualLeagueClientInstance.getVirtualRiotClient().getInstance();
        this.map.put(1, new Entitlement(virtualRiotClientInstance.getCookieSupplier(), 1));
        this.map.put(0, new Entitlement(virtualRiotClientInstance.getCookieSupplier(), 0));
        this.userinfo = new Userinfo(virtualRiotClientInstance.getCookieSupplier());
        this.virtualLeagueClientInstance = virtualLeagueClientInstance;
        this.gateway = virtualRiotClientInstance.getGateway();
    }

    @Override
    public List<ExceptionalRefreshable> getRefreshableList() {
        return Arrays.asList(new ExceptionalRefreshable[]{
                () -> ClientTokenStorage.this.tokenSupplier = virtualRiotClientInstance.getTokenSupplier(getAuthorizationSupplier().get()),
                () -> userinfo.authenticate(
                        gateway,
                        virtualRiotClientInstance.getRiotClientUserAgent("rso-auth"),
                        tokenSupplier
                ),
                () -> map.get(1).authenticate(
                        gateway,
                        virtualLeagueClientInstance.getLeagueClientUserAgent("rcp-be-entitlements"),
                        tokenSupplier
                ),
                () -> map.get(0).authenticate(
                        gateway,
                        virtualLeagueClientInstance.getLeagueClientUserAgent("rcp-be-entitlements"),
                        tokenSupplier
                )

        });
    }

    public Userinfo getUserinfo() {
        return userinfo;
    }

    public Map<Integer, Entitlement> getMap() {
        return map;
    }

    public StringTokenSupplier getTokenSupplier() {
        return tokenSupplier;
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

    @Override
    public Supplier<Authorization> getAuthorizationSupplier() {
        return () -> new Authorization.Builder()
                .setClaims("{\r\n    \"id_token\": {\r\n        \"rgn_EUW1\": null\r\n    },\r\n    \"userinfo\": {\r\n        \"rgn_EUW1\": null\r\n    }\r\n}")
                .setClientID(ClientID.LOL)
                .setRedirectURI("http://localhost/redirect")
                .setResponseTypes(ResponseType.TOKEN, ResponseType.ID_TOKEN)
                .setScopes(
                        ClientScope.OPENID,
                        ClientScope.OFFLINE_ACCESS,
                        ClientScope.LOL,
                        ClientScope.BAN,
                        ClientScope.PROFILE,
                        ClientScope.EMAIL,
                        ClientScope.PHONE,
                        ClientScope.BIRTHDATE,
                        ClientScope.ACCOUNT
                )
                .build();
    }
}
