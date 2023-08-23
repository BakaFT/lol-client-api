package com.hawolt.virtual.riotclient.instance;

import com.hawolt.cryptography.SHA256;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created: 17/08/2023 22:02
 * Author: Twitter @hawolt
 **/

public abstract class MultiFactorSupplier implements Supplier<String> {

    public static MultiFactorSupplier blank = new MultiFactorSupplier() {
        @Override
        public String get() {
            return null;
        }
    };

    private final Map<String, String> cache = new HashMap<>();

    public String get(String username, String password) {
        String identifier = SHA256.hash(username + password);
        if (!cache.containsKey(identifier)) cache.put(identifier, get());
        return cache.get(identifier);
    }

    public void clear(String username, String password) {
        cache.remove(SHA256.hash(username + password));
    }
}
