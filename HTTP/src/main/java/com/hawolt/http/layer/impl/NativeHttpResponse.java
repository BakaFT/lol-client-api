package com.hawolt.http.layer.impl;

import com.hawolt.http.NativeHttpClient;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.layer.IResponse;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

/**
 * Created: 20/08/2023 00:05
 * Author: Twitter @hawolt
 **/

public class NativeHttpResponse implements IResponse {
    private final Map<String, List<String>> requestHeaders, responseHeaders;
    private final String url, method;
    private final int code;
    private byte[] requestBody, responseBody;

    public static NativeHttpResponse from(HttpRequest request) throws IOException, InterruptedException {
        return new NativeHttpResponse(request, null);
    }

    public static NativeHttpResponse from(HttpRequest request, Gateway gateway) throws IOException, InterruptedException {
        return new NativeHttpResponse(request, gateway);
    }

    private NativeHttpResponse(HttpRequest request, Gateway gateway) throws IOException, InterruptedException {
        HttpClient client = NativeHttpClient.get(gateway);
        this.url = request.uri().toString();
        this.method = request.method();
        this.requestHeaders = request.headers().map();
        this.requestBody = request.bodyPublisher().map(p -> {
            var bodySubscriber = HttpResponse.BodySubscribers.ofByteArray();
            return bodySubscriber.getBody().toCompletableFuture().join();
        }).orElseGet(() -> new byte[0]);
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        this.responseHeaders = response.headers().map();
        this.code = response.statusCode();
        this.responseBody = response.body();
    }

    @Override
    public Map<String, List<String>> requestHeaders() {
        return requestHeaders;
    }

    @Override
    public Map<String, List<String>> headers() {
        return responseHeaders;
    }

    @Override
    public String asString() {
        return new String(responseBody);
    }

    @Override
    public byte[] response() {
        return responseBody;
    }

    @Override
    public byte[] request() {
        return requestBody;
    }

    @Override
    public String method() {
        return method;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public int code() {
        return code;
    }
}
