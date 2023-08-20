package com.hawolt.http;

import com.hawolt.http.auth.Authentication;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.layer.IResponse;
import com.hawolt.http.layer.impl.OkHttpResponse;
import com.hawolt.logger.Logger;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.net.Proxy;

/**
 * Created: 26/11/2022 13:55
 * Author: Twitter @hawolt
 **/

public class OkHttp3Client {
    public static boolean debug = true;
    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

    public static OkHttpClient get(Gateway gateway) {
        if (gateway == null) return okHttpClient;
        return get(gateway.getProxy(), gateway.getAuthentication());
    }

    private static OkHttpClient get(Proxy proxy, Authentication authentication) {
        if (proxy == null) return okHttpClient;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .proxy(proxy);
        return authentication == null ? builder.build() : builder.proxyAuthenticator(authentication).build();
    }

    public static Call perform(Request request) throws IOException {
        return perform(request, null);
    }

    public static Call perform(Request request, Gateway gateway) throws IOException {
        return get(gateway).newCall(request);
    }

    public static IResponse execute(Request request) throws IOException {
        return execute(request, null);
    }

    public static IResponse execute(Request request, Gateway gateway) throws IOException {
        IResponse response = OkHttpResponse.from(request, gateway);
        if (debug) Logger.debug("[http] {}", IResponse.translate(response));
        return response;
    }
}
