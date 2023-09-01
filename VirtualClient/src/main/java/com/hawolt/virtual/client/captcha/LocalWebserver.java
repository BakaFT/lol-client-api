package com.hawolt.virtual.client.captcha;

import com.hawolt.generic.util.Network;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import java.io.IOException;

/**
 * Created: 24/08/2023 17:58
 * Author: Twitter @hawolt
 **/

public class LocalWebserver {
    private P1Callback callback;
    public Javalin instance;
    private String rqData;
    private final int port;

    public LocalWebserver(int port) {
        this.port = port;
        this.instance =
                Javalin.create(config -> config.staticFiles.add("/html", Location.CLASSPATH))
                        .post("/v1/hcaptcha/response", context -> {
                            if (context.body().isEmpty() || !context.body().startsWith("P1")) {
                                context.status(403);
                            } else {
                                callback.onP1Token(context.body());
                                context.status(200);
                            }
                        })
                        .get("/v1/hcaptcha/rqdata", context -> {
                            context.result(rqData);
                        })
                        .before("/v1/*", context -> {
                            context.header("Access-Control-Allow-Origin", "*");
                        })
                        .start(port);
    }

    public void setRqData(String rqData) {
        this.rqData = rqData;
    }

    public void show(P1Callback callback) throws IOException {
        this.callback = callback;
        Network.browse(String.format("http://127.0.0.1:%s", port));
    }
}
