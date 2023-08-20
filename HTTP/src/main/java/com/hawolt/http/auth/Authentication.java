package com.hawolt.http.auth;

import okhttp3.*;

/**
 * Created: 18/07/2022 08:51
 * Author: Twitter @hawolt
 **/

public class Authentication implements Authenticator {
    private final String username, password;

    public Authentication(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public Request authenticate(Route route, Response response) {
        String credential = Credentials.basic(username, password);
        return response.request().newBuilder()
                .header("Proxy-Authorization", credential)
                .build();
    }

}
