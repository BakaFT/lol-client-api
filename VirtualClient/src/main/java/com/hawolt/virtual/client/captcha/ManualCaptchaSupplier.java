package com.hawolt.virtual.client.captcha;

import com.hawolt.exception.CaptchaException;
import com.hawolt.logger.Logger;
import com.hawolt.virtual.riotclient.instance.CaptchaSupplier;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created: 23/08/2023 10:32
 * Author: Twitter @hawolt
 **/

public class ManualCaptchaSupplier extends CaptchaSupplier implements P1Callback {
    private static final Random random = new Random();
    private final LocalWebserver webserver;

    private String p1Token;
    private int port;

    public ManualCaptchaSupplier() {
        this.webserver = new LocalWebserver();
        do {
            port = 50000 + random.nextInt(10000);
            try {
                webserver.start(port);
            } catch (Exception e) {
                Logger.warn("Unable to start Server on port {}, trying another...", port);
            }
        } while (!webserver.isRunning());
    }

    @Override
    public String solve(String userAgent, String rqData) throws IOException, CaptchaException, InterruptedException {
        this.webserver.setRqData(rqData);
        this.webserver.show(this);
        return waitForP1Token(System.currentTimeMillis());
    }

    public String waitForP1Token(long startedAt) throws InterruptedException, CaptchaException {
        do {
            Thread.sleep(1000L);
            if (System.currentTimeMillis() - startedAt >= TimeUnit.MINUTES.toMillis(2)) {
                throw new CaptchaException("RQData is no longer valid");
            }
        } while (p1Token == null);
        Logger.debug("shutting down local captcha file host");
        this.webserver.instance.stop();
        return p1Token;
    }

    @Override
    public void onP1Token(String token) {
        this.p1Token = token;
    }

    @Override
    public int getDestinationPort() throws InterruptedException {
        while (port == 0) {
            Thread.sleep(200L);
        }
        return port;
    }
}
