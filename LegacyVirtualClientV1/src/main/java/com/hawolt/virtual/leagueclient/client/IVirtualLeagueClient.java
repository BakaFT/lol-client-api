package com.hawolt.virtual.leagueclient.client;

import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.version.IVersionSupplier;
import com.hawolt.virtual.leagueclient.authentication.IAuthentication;
import com.hawolt.virtual.leagueclient.instance.IVirtualLeagueClientInstance;
import com.hawolt.virtual.leagueclient.refresh.IRefreshStatus;
import com.hawolt.virtual.leagueclient.refresh.RefreshGroup;
import com.hawolt.virtual.leagueclient.refresh.Refreshable;
import com.hawolt.virtual.leagueclient.refresh.ScheduledRefresh;
import com.hawolt.virtual.riotclient.client.IVirtualRiotClient;
import com.hawolt.virtual.riotclient.instance.IVirtualRiotClientInstance;

import java.util.Set;

/**
 * Created: 13/01/2023 11:46
 * Author: Twitter @hawolt
 **/

public interface IVirtualLeagueClient {

    ScheduledRefresh<?> refresh(IRefreshStatus status, IAuthentication authentication, IVersionSupplier versionSupplier, StringTokenSupplier tokenSupplier, long initial, long minutes);

    ScheduledRefresh<?> refresh(Refreshable refreshable, long initial, long minutes);

    ScheduledRefresh<?> refresh(RefreshGroup group, long initial, long minutes);

    void setAuthentication(Authentication type, IAuthentication authentication);

    IVirtualLeagueClientInstance getVirtualLeagueClientInstance();

    IVirtualRiotClientInstance getVirtualRiotClientInstance();

    Set<Authentication> getAvailableAuthenticators();

    IVirtualRiotClient getVirtualRiotClient();

    IAuthentication get(Authentication type);

    void onError(Throwable throwable);
}
