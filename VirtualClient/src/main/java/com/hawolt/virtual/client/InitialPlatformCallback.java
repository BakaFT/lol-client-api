package com.hawolt.virtual.client;

import com.hawolt.generic.data.Platform;

/**
 * Created: 03/10/2023 19:42
 * Author: Twitter @hawolt
 **/

public interface InitialPlatformCallback {
    InitialPlatformCallback BLANK = () -> {
        throw new RuntimeException("Please manually implement InitialPlatformCallback");
    };

    Platform getInitialPlatform();
}
