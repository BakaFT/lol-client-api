package com.hawolt.virtual.leagueclient.client;

import com.hawolt.virtual.leagueclient.instance.IVirtualLeagueClientInstance;
import com.hawolt.yaml.YamlWrapper;

/**
 * Created: 13/01/2023 11:46
 * Author: Twitter @hawolt
 **/

public class VirtualLeagueClient extends AbstractVirtualLeagueClient {
    //  private Map<WebOrigin, StringTokenSupplier> webOriginStringTokenSupplierMap;
    //   private Map<WebOrigin, OAuthToken> webOriginOAuthTokenMap;
    private YamlWrapper yamlWrapper;

    public VirtualLeagueClient(IVirtualLeagueClientInstance virtualLeagueClientInstance) {
        super(virtualLeagueClientInstance);
    }

  /*  public VirtualLeagueClient(IVirtualLeagueClientInstance virtualLeagueClientInstance) {
        super(virtualLeagueClientInstance);
    }*/

   /* public void setWebOriginStringTokenSupplierMap(Map<WebOrigin, StringTokenSupplier> webOriginStringTokenSupplierMap) {
        this.webOriginStringTokenSupplierMap = webOriginStringTokenSupplierMap;
    }

    public void setWebOriginOAuthTokenMap(Map<WebOrigin, OAuthToken> webOriginOAuthTokenMap) {
        this.webOriginOAuthTokenMap = webOriginOAuthTokenMap;
    }*/

   /* public void setYamlWrapper(YamlWrapper yamlWrapper) {
        this.yamlWrapper = yamlWrapper;
    }*/


 /*   public Map<WebOrigin, StringTokenSupplier> getWebOriginStringTokenSupplierMap() {
        return webOriginStringTokenSupplierMap;
    }

    public Map<WebOrigin, OAuthToken> getWebOriginOAuthTokenMap() {
        return webOriginOAuthTokenMap;
    }*/

   /* public YamlWrapper getYamlWrapper() {
        return yamlWrapper;
    }*/
}
