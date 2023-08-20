package com.hawolt.virtual.clientconfig;

import com.hawolt.generic.data.Platform;
import com.hawolt.http.OkHttp3Client;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.layer.IResponse;
import okhttp3.Request;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created: 18/08/2023 17:10
 * Author: Twitter @hawolt
 **/

public abstract class ClientConfig {
    protected final Platform platform;

    private JSONObject cache;

    public ClientConfig(Platform platform) {
        this.platform = platform;
    }

    public JSONObject load(Gateway gateway) throws IOException {
        if (cache != null) return cache;
        IResponse response = OkHttp3Client.execute(request(), gateway);
        this.cache = new JSONObject(response.asString());
        return cache;
    }

    protected abstract Request request();

    protected abstract String getType();

    public String getURL() {
        return String.format(
                "https://clientconfig.rpg.riotgames.com/api/v1/config/%s?app=league_of_legends&region=%s",
                getType(),
                platform.name()
        );
    }
}
