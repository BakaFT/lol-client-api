package com.hawolt.generic.data;

import com.hawolt.generic.token.impl.StringTokenSupplier;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Created: 13/01/2023 11:34
 * Author: Twitter @hawolt
 **/

public class QueryTokenParser {

    private static String getURI(String plaintext) throws IOException {
        if (plaintext.contains("auth_failure")) throw new IOException("AUTH_FAILURE");
        JSONObject object = new JSONObject(plaintext);
        if (!object.has("response")) throw new IOException("NO_RESPONSE_PRESENT");
        JSONObject nested = object.getJSONObject("response");
        if (!nested.has("parameters")) throw new IOException("NO_PARAMETERS_PRESENT");
        JSONObject parameters = nested.getJSONObject("parameters");
        if (!parameters.has("uri")) throw new IOException("NO_URI_PRESENT");
        return parameters.getString("uri");
    }

    private static StringTokenSupplier compute(String name, String[] data) throws IOException {
        if (data.length <= 1) throw new IOException("BAD_URI_FORMAT");
        StringTokenSupplier supplier = new StringTokenSupplier() {
            @Override
            public String getSupplierName() {
                return name;
            }
        };
        String query = data[1];
        String[] split = query.split("&");
        for (String parameter : split) {
            String[] pair = parameter.split("=");
            if (pair.length != 2) continue;
            String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8.name());
            String value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8.name());
            supplier.add(key, value);
        }
        return supplier;
    }

    public static StringTokenSupplier getOAuthValues(String name, String plaintext) throws IOException {
        return compute(name, getURI(plaintext).split("\\?"));
    }

    public static StringTokenSupplier getTokens(String name, String plaintext) throws IOException {
        return compute(name, getURI(plaintext).split("#"));
    }

    public static StringTokenSupplier getTokens(String name, String plaintext, String splitter) throws IOException {
        return compute(name, getURI(plaintext).split(splitter, 2));
    }
}
