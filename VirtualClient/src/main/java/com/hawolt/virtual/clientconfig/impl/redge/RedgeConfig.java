package com.hawolt.virtual.clientconfig.impl.redge;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created: 25/08/2023 20:02
 * Author: Twitter @hawolt
 **/

public class RedgeConfig {
    private Map<RedgeType, String> map = new HashMap<>();

    public RedgeConfig(JSONObject o) {
        JSONObject redges = o.getJSONObject("lol.game_client_settings.redge_urls.public");
        for (String key : redges.keySet()) {
            for (RedgeType type : RedgeType.values()) {
                if (type.toString().equals(key)) {
                    map.put(type, redges.getString(key));
                }
            }
        }
    }

    public String getRedgeValue(RedgeType type) {
        return map.getOrDefault(type, "UNAVAILABLE");
    }
}
