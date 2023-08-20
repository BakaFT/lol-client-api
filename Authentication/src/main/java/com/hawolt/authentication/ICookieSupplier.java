package com.hawolt.authentication;

import com.hawolt.generic.data.Platform;
import com.hawolt.http.auth.Gateway;
import com.hawolt.version.IVersionSupplier;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Created: 09/01/2023 21:17
 * Author: Twitter @hawolt
 **/

public interface ICookieSupplier {
    String getClientCookie(IVersionSupplier versionSupplier, CookieType type, Platform platform, Gateway gateway) throws IOException;

    String getClientCookie(IVersionSupplier versionSupplier, CookieType type, Platform platform) throws IOException;

    CompletableFuture<String> getWebCookie(IVersionSupplier versionSupplier, WebOrigin origin, Platform platform);

    String getClientCookie(CookieType type, Platform platform, Gateway gateway) throws IOException;

    String getClientCookie(CookieType type, Platform platform) throws IOException;

    CompletableFuture<String> getWebCookie(WebOrigin origin, Platform platform);
}
