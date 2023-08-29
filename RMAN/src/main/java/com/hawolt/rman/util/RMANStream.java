package com.hawolt.rman.util;

import com.github.luben.zstd.ZstdInputStream;
import com.hawolt.generic.util.RandomAccessReader;
import com.hawolt.rman.io.downloader.Bundle;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created: 29/08/2023 08:35
 * Author: Twitter @hawolt
 **/

public class RMANStream extends InputStream {
    private final ZstdInputStream zstdInputStream;

    private static class RMANSourceStream extends InputStream {

        private final RandomAccessReader reader;
        private final long lastIndex;

        public RMANSourceStream(Bundle bundle, int offset, long size) throws IOException {
            this.reader = new RandomAccessReader(bundle.getBytes());
            this.reader.seek(offset);
            this.lastIndex = offset + size;
        }

        @Override
        public int read() {
            if (reader.position() >= lastIndex) return -1;
            return reader.readByte() & 0xFF;
        }
    }

    public RMANStream(Bundle bundle, int offset, long size) throws IOException {
        this.zstdInputStream = new ZstdInputStream(new RMANSourceStream(bundle, offset, size));
    }

    @Override
    public int read() throws IOException {
        return zstdInputStream.read();
    }
}
