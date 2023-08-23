package com.hawolt.virtual.clientconfig.impl.rms;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created: 20/08/2023 02:30
 * Author: Twitter @hawolt
 **/

public class RiotMessageServiceConfig {
    private final Map<RMSAffinity, Map<String, String>> map = new HashMap<>();

    public RiotMessageServiceConfig(JSONObject o) {
        setRMSAffinity(RMSAffinity.AFFINITIES, o.getJSONObject("rms.affinities"));
        setRMSAffinity(RMSAffinity.ESPORTS, o.getJSONObject("rms.esports.affinities"));
        setRMSAffinity(RMSAffinity.LOLESPORTS, o.getJSONObject("rms.lolesports.affinities"));
        setRMSAffinity(RMSAffinity.RIOTESPORTS, o.getJSONObject("rms.riotesports.affinities"));
    }

    public void setRMSAffinity(RMSAffinity affinity, JSONObject o) {
        Map<String, String> affinities = new HashMap<>();
        for (String key : o.keySet()) {
            affinities.put(key, o.getString(key));
        }
        map.put(affinity, affinities);
    }

    public String getRiotMessageServiceAffinity(RMSAffinity affinity, String region) {
        return map.get(affinity).get(region);
    }
}
