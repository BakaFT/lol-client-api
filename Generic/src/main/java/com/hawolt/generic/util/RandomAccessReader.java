package com.hawolt.generic.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Created: 05/01/2023 12:06
 * Author: Twitter @hawolt
 **/

public class RandomAccessReader implements IBinaryReader {
    private final byte[] b;
    private int position;

    public RandomAccessReader(File file) throws IOException {
        this(file.toPath());
    }

    public RandomAccessReader(Path path) throws IOException {
        this(Files.readAllBytes(path));
    }

    public RandomAccessReader(byte[] b) {
        this.b = b;
    }

    public void seek(int position) {
        this.position = position;
    }

    public int position() {
        return position;
    }


    @Override
    public short readShort() {
        short ret = (short) ((b[position + 1] & 0xFF) << 8 | (b[position] & 0xFF));
        this.position += 2;
        return ret;
    }

    @Override
    public int readInt() {
        int ret = (b[position + 3] & 0xFF) << 24 | (b[position + 2] & 0xFF) << 16 | (b[position + 1] & 0xFF) << 8 | (b[position] & 0xFF);
        this.position += 4;
        return ret;
    }

    @Override
    public long readLong() {
        long ret = ((b[position + 7] & 0xFFL) << 56) |
                ((b[position + 6] & 0xFFL) << 48) |
                ((b[position + 5] & 0xFFL) << 40) |
                ((b[position + 4] & 0xFFL) << 32) |
                ((b[position + 3] & 0xFFL) << 24) |
                ((b[position + 2] & 0xFFL) << 16) |
                ((b[position + 1] & 0xFFL) << 8) |
                ((b[position] & 0xFFL));
        this.position += 8;
        return ret;
    }

    @Override
    public String readString(int n) {
        byte[] b = Arrays.copyOfRange(this.b, position, position + n);
        this.position += n;
        return new String(b);
    }

    @Override
    public byte readByte() {
        return b[position++];
    }

    @Override
    public byte[] readBytes(int n) {
        byte[] b = Arrays.copyOfRange(this.b, position, position + n);
        this.position += n;
        return b;
    }
}
