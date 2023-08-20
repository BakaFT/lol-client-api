package com.hawolt.http;

import com.hawolt.http.auth.Authentication;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.layer.IResponse;
import com.hawolt.http.layer.impl.NativeHttpResponse;
import com.hawolt.logger.Logger;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

/**
 * Created: 26/11/2022 13:55
 * Author: Twitter @hawolt
 **/

public class NativeHttpClient {
    public static boolean debug = true;
    private static final HttpClient client = HttpClient.newHttpClient();

    public static HttpClient get(Gateway gateway) {
        if (gateway == null) return client;
        return getClient(gateway);
    }

    private static HttpClient getClient(Gateway gateway) {
        if (gateway == null) return HttpClient.newHttpClient();
        Authentication authentication = gateway.getAuthentication();
        Proxy proxy = gateway.getProxy();
        return HttpClient.newBuilder()
                .proxy(ProxySelector.of((InetSocketAddress) proxy.address()))
                .authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(authentication.getUsername(), authentication.getPassword().toCharArray());
                    }
                })
                .build();
    }

    public static IResponse execute(HttpRequest request) throws IOException, InterruptedException {
        return execute(request, null);
    }

    public static IResponse execute(HttpRequest request, Gateway gateway) throws IOException, InterruptedException {
        IResponse response = NativeHttpResponse.from(request, gateway);
        if (debug) Logger.debug("[http] {}", IResponse.translate(response));
        return response;
    }
}
