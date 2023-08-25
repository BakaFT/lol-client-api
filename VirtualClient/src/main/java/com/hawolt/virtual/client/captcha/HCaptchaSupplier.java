package com.hawolt.virtual.client.captcha;

import com.hawolt.ICaptchaProvider;
import com.hawolt.ICaptchaResult;
import com.hawolt.ICaptchaSolver;
import com.hawolt.ICaptchaTask;
import com.hawolt.exception.CaptchaException;
import com.hawolt.exception.CaptchaLogicException;
import com.hawolt.impl.capsolver.CapSolver;
import com.hawolt.impl.capsolver.CapSolverBuilder;
import com.hawolt.virtual.riotclient.instance.CaptchaSupplier;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created: 23/08/2023 10:32
 * Author: Twitter @hawolt
 **/

public class HCaptchaSupplier extends CaptchaSupplier {
    private final CapSolver service;

    public HCaptchaSupplier(String key) {
        this.service = new CapSolver(key);
    }

    @Override
    public String solve(String userAgent, String rqData) throws IOException, CaptchaException, InterruptedException {
        ICaptchaSolver solver = service.getSolver();
        ICaptchaProvider provider = new CapSolverBuilder.HCaptchaTaskProxyless.Builder()
                .setEnterpriseData(new JSONObject().put("rqdata", rqData))
                .setWebsiteURL("https://authenticate.riotgames.com/api/v1/login")
                .setWebsiteKey("019f1553-3845-481c-a6f5-5a60ccf6d830")
                .setInvisible(true)
                .build();
        ICaptchaTask task = solver.create(provider.get());
        long timestamp = System.currentTimeMillis();
        ICaptchaResult result = null;
        while (result == null) {
            if ((System.currentTimeMillis() - timestamp) >= TimeUnit.MINUTES.toMillis(5)) {
                throw new CaptchaLogicException("Captcha Solver hit timeout");
            }
            ICaptchaResult temporary = task.get();
            if (temporary != null) result = temporary;
            else {
                Thread.sleep(2500L);
            }
        }
        return result.getResult();
    }
}
