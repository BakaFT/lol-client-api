package com.hawolt.generic.util;

import java.io.IOException;

/**
 * Created: 28/08/2023 21:47
 * Author: Twitter @hawolt
 **/

public class Network {
    public static void browse(String url) throws IOException {
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
