package com.hawolt.virtual.riotclient.client;

import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.virtual.leagueclient.authentication.impl.Entitlement;
import com.hawolt.virtual.leagueclient.exception.LeagueException;
import com.hawolt.virtual.leagueclient.instance.VirtualLeagueClientInstance;
import com.hawolt.virtual.leagueclient.userinfo.UserInformation;
import com.hawolt.virtual.refresh.IRefreshable;
import com.hawolt.virtual.refresh.ScheduledRefresh;
import com.hawolt.virtual.riotclient.instance.CaptchaSupplier;
import com.hawolt.virtual.riotclient.instance.IVirtualRiotClientInstance;
import com.hawolt.virtual.riotclient.instance.MultiFactorSupplier;
import com.hawolt.virtual.riotclient.userinfo.RiotClientUser;

import java.io.IOException;

/**
 * Created: 07/08/2023 16:54
 * Author: Twitter @hawolt
 **/

public interface IVirtualRiotClient {

    VirtualLeagueClientInstance createVirtualLeagueClientInstance() throws LeagueException, IOException;

    MultiFactorSupplier getMultifactorSupplier();

    StringTokenSupplier getRiotClientSupplier();

    UserInformation getClearUserinformation();

    ScheduledRefresh<?> getScheduledRefresh();

    IVirtualRiotClientInstance getInstance();

    CaptchaSupplier getCaptchaSupplier();

    RiotClientUser getRiotClientUser();

    IRefreshable getRefreshable();

    Entitlement getEntitlement();

    String getUsername();

    String getPassword();
}
