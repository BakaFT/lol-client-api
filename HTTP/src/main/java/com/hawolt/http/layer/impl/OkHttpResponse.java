package com.hawolt.http.layer.impl;

import com.hawolt.http.OkHttp3Client;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.layer.IResponse;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Pipe;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created: 20/08/2023 00:05
 * Author: Twitter @hawolt
 **/

public class OkHttpResponse implements IResponse {
    private final Map<String, List<String>> requestHeaders, responseHeaders;
    private final String url, method;
    private final int code;
    private byte[] requestBody, responseBody;

    public static OkHttpResponse from(Request request) throws IOException {
        return new OkHttpResponse(request, null);
    }

    public static OkHttpResponse from(Request request, Gateway gateway) throws IOException {
        return new OkHttpResponse(request, gateway);
    }

    private OkHttpResponse(Request request, Gateway gateway) throws IOException {
        this.requestHeaders = request.headers().toMultimap();
        this.url = request.url().toString();
        this.method = request.method();
        RequestBody body = request.body();
        if (body != null) {
            long size = body.contentLength() == 0 ? 1 : body.contentLength();
            Pipe pipe = new Pipe(size);
            BufferedSink sink = Okio.buffer(pipe.sink());
            body.writeTo(sink);
            this.requestBody = sink.getBuffer().readByteArray();
        }
        Call call = OkHttp3Client.get(gateway).newCall(request);
        try (Response response = call.execute()) {
            this.code = response.code();
            this.responseHeaders = response.headers().toMultimap();
            if (response.body() == null) return;
            this.responseBody = response.body().bytes();
        }
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
        return responseBody != null ? responseBody : new byte[0];
    }

    @Override
    public byte[] request() {
        return requestBody != null ? requestBody : new byte[0];
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
