package com.hawolt.virtual.riotclient.client;

import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.logger.Logger;
import com.hawolt.virtual.leagueclient.authentication.impl.Entitlement;
import com.hawolt.virtual.leagueclient.authentication.impl.Userinfo;
import com.hawolt.virtual.leagueclient.exception.LeagueException;
import com.hawolt.virtual.leagueclient.instance.VirtualLeagueClientInstance;
import com.hawolt.virtual.leagueclient.userinfo.UserInformation;
import com.hawolt.virtual.refresh.ExceptionalRefreshable;
import com.hawolt.virtual.refresh.IRefreshable;
import com.hawolt.virtual.refresh.RefreshManager;
import com.hawolt.virtual.refresh.ScheduledRefresh;
import com.hawolt.virtual.riotclient.instance.CaptchaSupplier;
import com.hawolt.virtual.riotclient.instance.IVirtualRiotClientInstance;
import com.hawolt.virtual.riotclient.instance.MultiFactorSupplier;
import com.hawolt.virtual.riotclient.userinfo.RiotClientUser;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Created: 26/11/2022 13:39
 * Author: Twitter @hawolt
 **/

public class VirtualRiotClient implements IVirtualRiotClient, IRefreshable {
    private final IVirtualRiotClientInstance instance;
    private final MultiFactorSupplier multifactor;
    private final CaptchaSupplier captchaSupplier;
    private final String username, password;

    private StringTokenSupplier riotClientSupplier;
    private ScheduledRefresh<?> scheduledRefresh;
    private UserInformation userInformation;
    private RiotClientUser riotClientUser;

    private Entitlement entitlement;

    public VirtualRiotClient(IVirtualRiotClientInstance instance, String username, String password, MultiFactorSupplier multifactor, CaptchaSupplier captchaSupplier) {
        this.riotClientSupplier = instance.getRiotClientTokenSupplier();
        this.captchaSupplier = captchaSupplier;
        this.multifactor = multifactor;
        this.username = username;
        this.password = password;
        this.instance = instance;
    }

    @Override
    public IVirtualRiotClientInstance getInstance() {
        return instance;
    }

    @Override
    public CaptchaSupplier getCaptchaSupplier() {
        return captchaSupplier;
    }

    @Override
    public StringTokenSupplier getRiotClientSupplier() {
        return riotClientSupplier;
    }

    @Override
    public UserInformation getClearUserinformation() {
        return userInformation;
    }

    @Override
    public ScheduledRefresh<?> getScheduledRefresh() {
        return scheduledRefresh;
    }

    @Override
    public RiotClientUser getRiotClientUser() {
        return riotClientUser;
    }

    @Override
    public IRefreshable getRefreshable() {
        return this;
    }

    @Override
    public Entitlement getEntitlement() {
        return entitlement;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public MultiFactorSupplier getMultifactorSupplier() {
        return multifactor;
    }

    @Override
    public VirtualLeagueClientInstance createVirtualLeagueClientInstance() throws IOException {
        String jwt = instance.getRiotClientTokenSupplier().getSimple("access_token");
        JSONObject object = new JSONObject(new String(Base64.getDecoder().decode(jwt.split("\\.")[1])));
        this.riotClientUser = new RiotClientUser(object);

        Userinfo clear = new Userinfo(instance.getCookieSupplier());
        clear.authenticate(instance.getGateway(), instance.getRiotClientUserAgent("rso-auth"), riotClientSupplier);
        this.userInformation = new UserInformation(new JSONObject(clear.getToken()));

        this.entitlement = new Entitlement(instance.getCookieSupplier(), 2);
        entitlement.authenticate(instance.getGateway(), instance.getRiotClientUserAgent("entitlements"), riotClientSupplier);

        this.scheduledRefresh = RefreshManager.submit(this, 55, 55);
        return new VirtualLeagueClientInstance(this);
    }

    @Override
    public List<ExceptionalRefreshable> getRefreshableList() {
        return Arrays.asList(new ExceptionalRefreshable[]{
                () -> VirtualRiotClient.this.riotClientSupplier = instance.getTokenSupplier(instance.getAuthorization().getAuthorizationSupplier().get()),
                () -> entitlement.refresh(instance.getGateway(), instance.getRiotClientUserAgent("entitlements"), VirtualRiotClient.this.riotClientSupplier)
        });
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
                Logger.error(e);
            }
        }
    }
}
