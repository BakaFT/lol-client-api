package com.hawolt.virtual.client;

import com.hawolt.logger.Logger;

/**
 * Created: 23/08/2023 11:38
 * Author: Twitter @hawolt
 **/

public class LoginStateConsumer implements ILoginStateConsumer {
    @Override
    public void onStateChange(LoginState state) {
        Logger.debug("login state changed to {}", state);
    }

    @Override
    public void onException(Exception e) {
        Logger.error(e);
    }
}
