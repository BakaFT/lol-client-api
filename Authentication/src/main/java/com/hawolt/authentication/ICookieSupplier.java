package com.hawolt.authentication;

import com.hawolt.http.layer.IResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

/**
 * Created: 09/01/2023 21:17
 * Author: Twitter @hawolt
 **/

public interface ICookieSupplier {
    static String generateClientNonce() {
        return new String(Base64.getEncoder().encode(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8))).substring(0, 22);
    }

    IResponse post(String userAgent, Authorization authorization) throws IOException;

    void configure(String userAgent) throws IOException;

    IResponse handle(IResponse response);

    String getCookie(String hostname);

    boolean has(String cookie);
}
