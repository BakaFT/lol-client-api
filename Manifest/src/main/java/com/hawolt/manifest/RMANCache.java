package com.hawolt.manifest;

import com.hawolt.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created: 13/01/2023 16:45
 * Author: Twitter @hawolt
 **/

public class RMANCache {
    private static final Path path = Paths.get(System.getProperty("java.io.tmpdir")).resolve("client-rman-cache");
    private static final Map<String, byte[]> storage = new HashMap<>();
    private static final Set<String> accessed = new HashSet<>();

    public static void preload() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            Files.createDirectories(path);
            File[] files = path.toFile().listFiles();
            if (files == null) return;
            for (File file : files) {
                service.execute(() -> {
                    try {
                        loadToMemory(file.getName());
                    } catch (IOException e) {
                        Logger.error(e);
                    }
                });
            }
        } catch (IOException e) {
            Logger.debug("Unable to create cache directory");
        }
        service.shutdown();
    }

    public static void cleanup() {
        clearMemoryMapping();
        purge();
    }

    public static void purge() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Set<String> names = getUsedFiles();
        service.execute(() -> {
            File[] files = path.toFile().listFiles();
            if (files == null) return;
            for (File file : files) {
                if (!names.contains(file.getName()) && file.delete()) {
                    Logger.debug("[rman-cache] remove old file from cache cache: {}", file.getName());
                }
            }
        });
        service.shutdown();
    }

    public static Set<String> getUsedFiles() {
        return accessed;
    }

    public static void clearMemoryMapping() {
        storage.replaceAll((k, v) -> new byte[0]);
    }

    public static boolean isCached(String name) {
        return storage.containsKey(name);
    }

    private static byte[] loadToMemory(String name) throws IOException {
        byte[] bytes = Files.readAllBytes(path.resolve(name));
        storage.put(name, bytes);
        return bytes;
    }

    public static byte[] load(String name) throws IOException {
        accessed.add(name);
        if (storage.containsKey(name)) return storage.get(name);
        return loadToMemory(name);
    }

    public static void store(String name, Path origin) throws IOException {
        if (!path.toFile().exists()) return;
        Logger.debug("[rman-cache] storing file in cache: {}", name);
        Files.move(origin, path.resolve(name), StandardCopyOption.REPLACE_EXISTING);
        load(name);
    }

    public static void store(String name, byte[] b) throws IOException {
        if (!path.toFile().exists()) return;
        Logger.debug("[rman-cache] storing file in cache: {}", name);
        Files.write(path.resolve(name), b, StandardOpenOption.CREATE);
        load(name);
    }
}
