package com.hawolt.virtual.riotclient.instance;

import com.hawolt.http.layer.IResponse;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created: 23/08/2023 12:57
 * Author: Twitter @hawolt
 **/

public class CaptchaInfo {
    private final Map<String, CaptchaInstance> map = new HashMap<>();
    private final IResponse response;
    private final String type;

    public CaptchaInfo(IResponse response) {
        this.response = response;
        JSONObject object = new JSONObject(response.asString());
        JSONObject captcha = object.getJSONObject("captcha");
        this.type = captcha.getString("type");
        captcha.remove("type");
        for (String key : captcha.keySet()) {
            map.put(key, new CaptchaInstance(captcha.getJSONObject(key)));
        }
    }

    public CaptchaInstance getCurrentCaptchaInstance() {
        return map.get(type);
    }

    public String getType() {
        return type;
    }

    public IResponse getResponse() {
        return response;
    }
}
