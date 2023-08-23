package com.hawolt.virtual.client;

import com.hawolt.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created: 23/08/2023 14:02
 * Author: Twitter @hawolt
 **/

public class CookieManager {
    private final Map<String, String> cookies = new HashMap<>();

    public void addCookie(String name, String value) {
        Logger.debug("adding cookie: {}={}", name, value);
        cookies.put(name, value);
    }

    public void addCookiesFromList(List<String> list) {
        if (list == null || list.isEmpty()) return;
        for (String string : list) {
            String[] data = string.split(";");
            String[] pair = data[0].split("=");
            addCookie(pair[0], pair[1]);
        }
    }

    public void addCookiesFromCookieString(String cookie) {
        String[] cookies = cookie.split(";");
        for (String s : cookies) {
            String[] pair = s.split("=");
            addCookie(pair[0], pair[1]);
        }
    }

    public String cook() {
        StringBuilder builder = new StringBuilder();
        List<String> keys = new ArrayList<>(cookies.keySet());
        for (int i = 0; i < keys.size(); i++) {
            if (i != 0) builder.append("; ");
            String cookie = keys.get(i);
            builder.append(cookie).append("=").append(cookies.get(cookie));
        }
        return builder.toString();
    }
}
