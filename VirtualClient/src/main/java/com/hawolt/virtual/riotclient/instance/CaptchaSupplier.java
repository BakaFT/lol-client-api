package com.hawolt.virtual.riotclient.instance;

import com.hawolt.exception.CaptchaException;

import java.io.IOException;

/**
 * Created: 17/08/2023 22:02
 * Author: Twitter @hawolt
 **/

public abstract class CaptchaSupplier {

    public static CaptchaSupplier blank;

    static {
        blank = new CaptchaSupplier() {

            @Override
            public String solve(String userAgent, String rqData) {
                return null;
            }
        };
    }

    public abstract String solve(String userAgent, String rqData) throws IOException, CaptchaException, InterruptedException;

}
