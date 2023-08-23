package com.hawolt.virtual.client;

/**
 * Created: 23/08/2023 11:35
 * Author: Twitter @hawolt
 **/

public interface ILoginStateConsumer {
    void onStateChange(LoginState state);

    void onException(Exception e);
}
