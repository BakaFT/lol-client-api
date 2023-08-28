package com.hawolt.virtual.client.captcha;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import java.io.IOException;

/**
 * Created: 24/08/2023 17:58
 * Author: Twitter @hawolt
 **/

public class LocalWebserver {
    private static P1Callback callback;
    public static Javalin instance;
    private static String rqData;

    static {
        LocalWebserver.instance =
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
                        .start(42069);
    }

    public static void setRqData(String rqData) {
        LocalWebserver.rqData = rqData;
    }

    public static void show(P1Callback callback) throws IOException {
        LocalWebserver.callback = callback;
        browse("http://127.0.0.1:42069");
    }

    private static void browse(String url) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        Runtime rt = Runtime.getRuntime();
        if (os.contains("mac")) {
            rt.exec("open " + url);
        } else if (os.contains("nix") || os.contains("nux")) {
            rt.exec(new String[]{"xdg-open", url});
        } else {
            rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
        }
    }
}
