package com.hawolt.virtual.client.captcha;

import com.hawolt.exception.CaptchaException;
import com.hawolt.virtual.riotclient.instance.CaptchaSupplier;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created: 23/08/2023 10:32
 * Author: Twitter @hawolt
 **/

public class ManualCaptchaSupplier extends CaptchaSupplier implements P1Callback {

    private String p1Token;

    @Override
    public String solve(String userAgent, String rqData) throws IOException, CaptchaException, InterruptedException {
        LocalWebserver.setRqData(rqData);
        LocalWebserver.show(this);
        return waitForP1Token(System.currentTimeMillis());
    }

    public String waitForP1Token(long startedAt) throws InterruptedException, CaptchaException {
        do {
            Thread.sleep(1000L);
            if (System.currentTimeMillis() - startedAt >= TimeUnit.MINUTES.toMillis(2)) {
                throw new CaptchaException("RQData is no longer valid");
            }
        } while (p1Token == null);
        return p1Token;
    }

    @Override
    public void onP1Token(String token) {
        this.p1Token = token;
    }
}
