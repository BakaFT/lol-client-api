package com.hawolt.virtual.leagueclient.client;

import com.hawolt.authentication.WebOrigin;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.virtual.leagueclient.authentication.OAuthToken;
import com.hawolt.virtual.leagueclient.instance.IVirtualLeagueClientInstance;
import com.hawolt.yaml.YamlWrapper;

import java.util.Map;

/**
 * Created: 13/01/2023 11:46
 * Author: Twitter @hawolt
 **/

public class VirtualLeagueClient extends AbstractVirtualLeagueClient {
    private Map<WebOrigin, StringTokenSupplier> webOriginStringTokenSupplierMap;
    private Map<WebOrigin, OAuthToken> webOriginOAuthTokenMap;
    private YamlWrapper yamlWrapper;

    public VirtualLeagueClient(IVirtualLeagueClientInstance virtualLeagueClientInstance) {
        super(virtualLeagueClientInstance);
    }

    public void setWebOriginStringTokenSupplierMap(Map<WebOrigin, StringTokenSupplier> webOriginStringTokenSupplierMap) {
        this.webOriginStringTokenSupplierMap = webOriginStringTokenSupplierMap;
    }

    public void setWebOriginOAuthTokenMap(Map<WebOrigin, OAuthToken> webOriginOAuthTokenMap) {
        this.webOriginOAuthTokenMap = webOriginOAuthTokenMap;
    }

    public void setYamlWrapper(YamlWrapper yamlWrapper) {
        this.yamlWrapper = yamlWrapper;
    }


    public Map<WebOrigin, StringTokenSupplier> getWebOriginStringTokenSupplierMap() {
        return webOriginStringTokenSupplierMap;
    }

    public Map<WebOrigin, OAuthToken> getWebOriginOAuthTokenMap() {
        return webOriginOAuthTokenMap;
    }

    public YamlWrapper getYamlWrapper() {
        return yamlWrapper;
    }
}
