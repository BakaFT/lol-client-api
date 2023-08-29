package com.hawolt.rman.body;

import com.hawolt.rman.util.Hex;

/**
 * Created: 05/01/2023 12:37
 * Author: Twitter @hawolt
 **/

public class RMANFileBodyBundleChunk {
    private int compressedSize;
    private int uncompressedSize;
    private long chunkId;

    public int getCompressedSize() {
        return compressedSize;
    }

    public void setCompressedSize(int compressedSize) {
        this.compressedSize = compressedSize;
    }

    public int getUncompressedSize() {
        return uncompressedSize;
    }

    public void setUncompressedSize(int uncompressedSize) {
        this.uncompressedSize = uncompressedSize;
    }

    public long getChunkId() {
        return chunkId;
    }

    public void setChunkId(long chunkId) {
        this.chunkId = chunkId;
    }

    @Override
    public String toString() {
        return "RMANFileBodyBundleChunk{" +
                "compressedSize=" + compressedSize +
                ", uncompressedSize=" + uncompressedSize +
                ", chunkId='" + Hex.from(chunkId, 16) + '\'' +
                '}';
    }
}
