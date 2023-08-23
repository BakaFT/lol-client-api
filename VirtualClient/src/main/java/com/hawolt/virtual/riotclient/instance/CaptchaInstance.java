package com.hawolt.virtual.riotclient.instance;

import org.json.JSONObject;

/**
 * Created: 23/08/2023 12:59
 * Author: Twitter @hawolt
 **/

public class CaptchaInstance extends JSONObject {
    public CaptchaInstance(JSONObject object) {
        for (String key : object.keySet()) {
            put(key, object.get(key));
        }
    }
}
