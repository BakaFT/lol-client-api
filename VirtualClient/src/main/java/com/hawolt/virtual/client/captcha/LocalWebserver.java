package com.hawolt.virtual.client.captcha;

import com.hawolt.generic.util.Network;
import com.hawolt.logger.Logger;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import java.io.IOException;

/**
 * Created: 24/08/2023 17:58
 * Author: Twitter @hawolt
 **/

public class LocalWebserver {
    private P1Callback callback;
    private boolean started;
    public Javalin instance;
    private String rqData;

    private Javalin create(int port) {
        return Javalin.create(config -> config.staticFiles.add("/html", Location.CLASSPATH))
                .events(event -> {
                    event.serverStartFailed(() -> Logger.warn("Failed to start local server on port {}, trying another", port));
                    event.serverStarted(() -> started = true);
                })
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
                });
    }

    public boolean isRunning() {
        return started;
    }

    public void start(int port) {
        this.instance = create(port);
        this.instance.start(port);
    }

    public void setRqData(String rqData) {
        this.rqData = rqData;
    }

    public void show(P1Callback callback) throws IOException, InterruptedException {
        this.callback = callback;
        Network.browse(String.format("http://127.0.0.1:%s", callback.getDestinationPort()));
    }
}
