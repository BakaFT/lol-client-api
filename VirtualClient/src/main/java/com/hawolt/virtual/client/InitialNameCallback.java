package com.hawolt.virtual.client;

/**
 * Created: 03/10/2023 20:31
 * Author: Twitter @hawolt
 **/

public interface InitialNameCallback {
    InitialNameCallback BLANK = () -> {
        throw new RuntimeException("Please manually implement InitialNameCallback or set ignoreSummoner to true");
    };

    String getInitialName();
}
