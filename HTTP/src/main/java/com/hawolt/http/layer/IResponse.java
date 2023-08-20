package com.hawolt.http.layer;

import com.hawolt.http.integrity.Diffuser;

import java.util.List;
import java.util.Map;

/**
 * Created: 20/08/2023 00:04
 * Author: Twitter @hawolt
 **/

public interface IResponse {
    static String translate(IResponse response) {
        StringBuilder builder = new StringBuilder()
                .append(System.lineSeparator())
                .append(response.method())
                .append(" ")
                .append(response.url());
        Map<String, List<String>> headers = response.headers();
        for (String key : headers.keySet()) {
            List<String> collection = headers.get(key);
            for (String header : collection) {
                builder.append(System.lineSeparator()).append(key).append(": ").append(header);
            }
        }
        String content = Diffuser.vaporize(new String(response.request()));
        builder.append(System.lineSeparator()).append(content);
        builder.append(System.lineSeparator()).append(response.asString());
        return builder.toString();
    }

    Map<String, List<String>> requestHeaders();

    Map<String, List<String>> headers();

    String asString();

    byte[] response();

    byte[] request();

    String method();


    String url();

    int code();
}
