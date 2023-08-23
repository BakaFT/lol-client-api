package com.hawolt.virtual.leagueclient.instance;

import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.virtual.leagueclient.authentication.OAuthToken;
import com.hawolt.virtual.leagueclient.userinfo.UserInformation;
import com.hawolt.virtual.riotclient.client.IVirtualRiotClient;
import com.hawolt.yaml.IYamlSupplier;

/**
 * Created: 13/01/2023 11:46
 * Author: Twitter @hawolt
 **/

public class VirtualLeagueClientInstance extends AbstractVirtualLeagueClientInstance {

    public VirtualLeagueClientInstance(IVirtualRiotClient virtualRiotClient, UserInformation userInformation, IYamlSupplier yamlSupplier, StringTokenSupplier leagueClientSupplier, boolean selfUpdate) {
        this(virtualRiotClient, userInformation, yamlSupplier, leagueClientSupplier, null, selfUpdate);
    }

    public VirtualLeagueClientInstance(IVirtualRiotClient virtualRiotClient, UserInformation userInformation, IYamlSupplier yamlSupplier, StringTokenSupplier leagueClientSupplier, OAuthToken token, boolean selfUpdate) {
        super(virtualRiotClient, userInformation, yamlSupplier, leagueClientSupplier, token, selfUpdate);
    }

    public IYamlSupplier getYamlSupplier() {
        return yamlSupplier;
    }

    public boolean isSelfUpdate() {
        return selfUpdate;
    }
}
