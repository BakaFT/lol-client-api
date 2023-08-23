package com.hawolt.generic.token.impl;

import com.hawolt.generic.token.ITokenSupplier;
import com.hawolt.logger.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created: 10/01/2023 23:25
 * Author: Twitter @hawolt
 **/

public class StringTokenSupplier implements ITokenSupplier<String> {
    public static boolean debug = true;
    private final Map<String, String> map = new HashMap<>();

    private String name;

    @Override
    public String get(String name) {
        return get(name, false);
    }

    public String get(String name, boolean raw) {
        String key = raw ? name : String.join(".", getSupplierName(), name);
        String value = map.getOrDefault(key, "null");
        if (debug) Logger.debug("[{}] fetching {}: {}", getSupplierName(), name, value);
        return value;
    }

    public String getSimple(String name) {
        for (String key : map.keySet()) {
            if (key.endsWith(name)) return map.get(key);
        }
        return "null";
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public void add(String k, String v) {
        add(k, v, false);
    }

    public void add(String k, String v, boolean raw) {
        String key = raw ? k : String.join(".", getSupplierName(), k);
        if (debug) Logger.debug("[{}] putting {}: {}", getSupplierName(), k, v);
        map.put(key, v);
    }

    @Override
    public boolean has(String s) {
        return map.containsKey(s);
    }

    @Override
    public String getSupplierName() {
        return getClass().getSimpleName().toLowerCase();
    }

    public static StringTokenSupplier merge(String name, StringTokenSupplier... suppliers) {
        String bundled = Arrays.stream(suppliers).map(StringTokenSupplier::getSupplierName).collect(Collectors.joining(","));
        if (debug) Logger.debug("[token-merge] merging [{}] into: {}", bundled, name);
        StringTokenSupplier supplier = new StringTokenSupplier() {
            @Override
            public String getSupplierName() {
                return name;
            }
        };
        for (StringTokenSupplier i : suppliers) {
            for (String key : i.keySet()) {
                supplier.add(key, i.get(key, true));
            }
        }
        return supplier;
    }
}
