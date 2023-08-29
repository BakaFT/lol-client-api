package com.hawolt.rman;

import com.hawolt.rman.body.*;
import com.hawolt.rman.header.RMANFileHeader;
import com.hawolt.rman.io.downloader.BadBundleException;
import com.hawolt.rman.io.downloader.Bundle;
import com.hawolt.rman.util.Hex;
import com.hawolt.rman.util.RMANStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created: 01/01/2023 03:09
 * Author: Twitter @hawolt
 **/

public class RMANFile {
    private final Map<Long, RMANFileBodyBundleChunkInfo> chunksById = new HashMap<>();
    private final Map<Long, RMANFileBodyBundle> bundlesById = new HashMap<>();
    private RMANFileHeader header;
    private RMANFileBody body;
    private byte[] compressedBody, signature;

    public void buildChunkMap() {
        for (RMANFileBodyBundle bundle : getBody().getBundles()) {
            int currentIndex = 0;
            for (RMANFileBodyBundleChunk chunk : bundle.getChunks()) {
                RMANFileBodyBundleChunkInfo chunkInfo = new RMANFileBodyBundleChunkInfo(bundle.getBundleId(), chunk.getChunkId(), currentIndex, chunk.getCompressedSize());
                chunksById.put(chunk.getChunkId(), chunkInfo);
                currentIndex += chunk.getCompressedSize();
            }
        }
    }

    public void buildBundleMap() {
        for (RMANFileBodyBundle bundle : getBody().getBundles()) {
            bundlesById.put(bundle.getBundleId(), bundle);
        }
    }

    public Set<RMANFileBodyBundle> getBundlesForFile(RMANFileBodyFile file) {
        return file.getChunkIds()
                .stream()
                .map(getChunkMap()::get)
                .map(RMANFileBodyBundleChunkInfo::getBundleId)
                .map(getBundleMap()::get)
                .collect(Collectors.toSet());
    }

    private void handle(RMANFileBodyFile file, List<Bundle> list, OutputStream outputStream) throws IOException {
        List<Long> chunkIds = file.getChunkIds();
        try (OutputStream stream = outputStream) {
            for (long chunkId : chunkIds) {
                int read;
                byte[] bytes = new byte[8192];
                try (RMANStream rmanStream = load(list, chunkId)) {
                    while ((read = rmanStream.read(bytes)) != -1) {
                        stream.write(bytes, 0, read);
                    }
                }
                //TODO WRITE OWN ZSTD IMPLEMENTATION
                System.gc();
            }
        }
    }

    public byte[] extract(RMANFileBodyFile file, List<Bundle> list) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        handle(file, list, baos);
        return baos.toByteArray();
    }

    public void extract(RMANFileBodyFile file, List<Bundle> list, File location) throws IOException {
        handle(file, list, Files.newOutputStream(location.toPath()));
    }

    private RMANStream load(List<Bundle> list, long chunkId) throws IOException {
        RMANFileBodyBundleChunkInfo current = chunksById.get(chunkId);
        String name = String.join(".", Hex.from(current.getBundleId(), 16), "bundle");
        List<Bundle> bundles = list.stream().filter(bundle -> bundle.getName().equals(name)).collect(Collectors.toList());
        if (bundles.size() != 1) throw new BadBundleException("Bad Bundle: " + name);
        return new RMANStream(bundles.remove(0), current.getOffsetToChunk(), current.getCompressedSize());
    }

    public Map<Long, RMANFileBodyBundleChunkInfo> getChunkMap() {
        return chunksById;
    }

    public Map<Long, RMANFileBodyBundle> getBundleMap() {
        return bundlesById;
    }

    public RMANFileHeader getHeader() {
        return header;
    }

    public void setHeader(RMANFileHeader header) {
        this.header = header;
    }

    public RMANFileBody getBody() {
        return body;
    }

    public void setBody(RMANFileBody body) {
        this.body = body;
    }

    public byte[] getCompressedBody() {
        return compressedBody;
    }

    public void setCompressedBody(byte[] compressedBody) {
        this.compressedBody = compressedBody;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }
}
