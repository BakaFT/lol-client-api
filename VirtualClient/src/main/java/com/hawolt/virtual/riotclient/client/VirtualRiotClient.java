package com.hawolt.virtual.riotclient.client;

import com.hawolt.authentication.CookieType;
import com.hawolt.generic.data.Platform;
import com.hawolt.generic.data.QueryTokenParser;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.virtual.client.LoginState;
import com.hawolt.virtual.leagueclient.authentication.OAuthToken;
import com.hawolt.virtual.leagueclient.authentication.Userinfo;
import com.hawolt.virtual.leagueclient.exception.LeagueException;
import com.hawolt.virtual.leagueclient.instance.VirtualLeagueClientInstance;
import com.hawolt.virtual.leagueclient.userinfo.UserInformation;
import com.hawolt.virtual.riotclient.instance.CaptchaSupplier;
import com.hawolt.virtual.riotclient.instance.IVirtualRiotClientInstance;
import com.hawolt.virtual.riotclient.instance.MultiFactorSupplier;
import com.hawolt.virtual.riotclient.userinfo.RiotClientUser;
import com.hawolt.yaml.IYamlSupplier;
import com.hawolt.yaml.impl.YamlSupplier;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Base64;

/**
 * Created: 26/11/2022 13:39
 * Author: Twitter @hawolt
 **/

public class VirtualRiotClient implements IVirtualRiotClient {
    private final StringTokenSupplier riotClientSupplier;
    private final IVirtualRiotClientInstance instance;
    private final RiotClientUser riotClientUser;
    private MultiFactorSupplier multifactor;
    private CaptchaSupplier captchaSupplier;
    private String username, password;

    public VirtualRiotClient(
            IVirtualRiotClientInstance instance,
            String username,
            String password,
            MultiFactorSupplier multifactor,
            CaptchaSupplier captchaSupplier,
            StringTokenSupplier riotClientSupplier
    ) {
        this.riotClientUser = new RiotClientUser(riotClientSupplier.get("access_token"));
        this.riotClientSupplier = riotClientSupplier;
        this.captchaSupplier = captchaSupplier;
        this.multifactor = multifactor;
        this.username = username;
        this.password = password;
        this.instance = instance;
    }

    public VirtualRiotClient(IVirtualRiotClientInstance instance, OAuthToken oauth) {
        this.riotClientUser = new RiotClientUser(oauth.get("access_token"));
        this.riotClientSupplier = oauth;
        this.instance = instance;
    }

    public VirtualLeagueClientInstance createVirtualLeagueClientInstance(StringTokenSupplier supplier, boolean selfUpdate) throws IOException {
        Userinfo clear = new Userinfo();
        String jwt = clear.authenticate(instance.getGateway(), instance.getLocalRiotFileVersion(), supplier);
        String decrypted = new String(Base64.getDecoder().decode(jwt.split("\\.")[1]));
        UserInformation userInformation = new UserInformation(new JSONObject(decrypted));
        Platform platform = Platform.valueOf(riotClientUser.getDataRegion());
        IYamlSupplier yamlSupplier = new YamlSupplier(platform);
        return new VirtualLeagueClientInstance(this, userInformation, yamlSupplier, supplier, (OAuthToken) supplier, selfUpdate);
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
    public RiotClientUser getRiotClientUser() {
        return riotClientUser;
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
    public VirtualLeagueClientInstance createVirtualLeagueClientInstance() throws LeagueException, IOException {
        return createVirtualLeagueClientInstance(false);
    }

    @Override
    public MultiFactorSupplier getMultifactorSupplier() {
        return multifactor;
    }

    @Override
    public VirtualLeagueClientInstance createVirtualLeagueClientInstance(boolean selfUpdate) throws LeagueException, IOException {
        if (!riotClientUser.isLeagueAccountAssociated()) {
            throw new LeagueException(LeagueException.ErrorType.NO_LEAGUE_ACCOUNT);
        }
        Platform platform = Platform.valueOf(riotClientUser.getDataRegion());
        IYamlSupplier yamlSupplier = new YamlSupplier(platform);
        StringTokenSupplier leagueClientSupplier = QueryTokenParser.getTokens(
                "lol",
                instance.get(
                        username,
                        password,
                        CookieType.LOL,
                        multifactor,
                        captchaSupplier,
                        instance.getGateway(),
                        LoginState.LEAGUE_COOKIE
                )
        );
        Userinfo clear = new Userinfo();
        UserInformation userInformation = new UserInformation(new JSONObject(clear.authenticate(instance.getGateway(), instance.getLocalRiotFileVersion(), riotClientSupplier)));
        return new VirtualLeagueClientInstance(this, userInformation, yamlSupplier, leagueClientSupplier, selfUpdate);
    }
}
