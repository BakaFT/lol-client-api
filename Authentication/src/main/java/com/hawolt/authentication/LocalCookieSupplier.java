package com.hawolt.authentication;

import com.hawolt.generic.Constant;
import com.hawolt.http.OkHttp3Client;
import com.hawolt.http.layer.IResponse;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created: 09/01/2023 21:17
 * Author: Twitter @hawolt
 **/

public class LocalCookieSupplier implements ICookieSupplier {

    private final Map<String, Cookie> map = new HashMap<>();
    private final Object lock = new Object();

    @Override
    public IResponse handle(IResponse response) {
        synchronized (lock) {
            List<String> base = response.headers().get("set-cookie");
            if (base == null) return response;
            List<Cookie> list = base.stream()
                    .map(line -> new Cookie(response.url(), line))
                    .toList();
            for (Cookie cookie : list) {
                map.put(cookie.getName(), cookie);
            }
        }
        return response;
    }

    @Override
    public String getCookie(String hostname) {
        synchronized (lock) {
            return map.values().stream()
                    .filter(cookie -> cookie.isValidFor(hostname))
                    .filter(Cookie::isNotExpired)
                    .map(Cookie::get)
                    .collect(Collectors.joining("; "));
        }
    }

    @Override
    public boolean has(String cookie) {
        synchronized (lock) {
            return map.values().stream()
                    .filter(Cookie::isNotExpired)
                    .anyMatch(o -> o.getName().equalsIgnoreCase(cookie));
        }
    }

    @Override
    public void configure(String userAgent) throws IOException {
        Authorization authorization = new Authorization.Builder()
                .setClientID(ClientID.RIOT_CLIENT)
                .setRedirectURI("http://localhost/redirect")
                .setResponseTypes(ResponseType.TOKEN, ResponseType.ID_TOKEN)
                .setScopes(
                        ClientScope.OPENID,
                        ClientScope.LINK,
                        ClientScope.BAN,
                        ClientScope.LOL_REGION,
                        ClientScope.ACCOUNT
                )
                .build();
        post(userAgent, authorization);
        if (!has("__cf_bm")) throw new IOException("Unable to obtain __cf_bm Cookie, please try again later");
    }


    @Override
    public IResponse post(String userAgent, Authorization authorization) throws IOException {
        RequestBody payload = RequestBody.create(authorization.toString(), Constant.APPLICATION_JSON);
        Request.Builder builder = new Request.Builder()
                .url("https://auth.riotgames.com/api/v1/authorization")
                .addHeader("User-Agent", userAgent)
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .post(payload);
        String cookie = getCookie("riotgames.com");
        if (cookie != null && !cookie.isEmpty()) builder.addHeader("Cookie", cookie);
        return handle(OkHttp3Client.execute(builder.build()));
    }

}
