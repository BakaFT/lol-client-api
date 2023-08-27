package com.hawolt.http.integrity;

import com.hawolt.http.layer.IResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created: 05/02/2023 14:09
 * Author: Twitter @hawolt
 **/

public class Diffuser {
    public static boolean PRIVACY_ENHANCEMENT = true;
    private final static Set<String> set = new HashSet<>();

    public static void add(String o) {
        set.add(o);
    }

    public static String vaporize(String tarnished) {
        if (!PRIVACY_ENHANCEMENT) return tarnished;
        for (String key : new ArrayList<>(set)) {
            if (tarnished.contains(key)) tarnished = tarnished.replace(key, IResponse.DEFAULT);
        }
        return tarnished;
    }
}
