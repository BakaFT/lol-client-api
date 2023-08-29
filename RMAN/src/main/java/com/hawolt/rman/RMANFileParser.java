package com.hawolt.rman;

import com.github.luben.zstd.Zstd;
import com.hawolt.generic.util.RandomAccessReader;
import com.hawolt.rman.body.*;
import com.hawolt.rman.header.RMANFileHeader;

import java.util.ArrayList;
import java.util.List;

public class RMANFileParser {

    public static RMANFile parse(byte[] b) {
        RandomAccessReader reader = new RandomAccessReader(b);
        RMANFile file = new RMANFile();
        RMANFileHeader header = header(reader);
        file.setHeader(header);
        reader.seek(header.getOffset());
        file.setCompressedBody(reader.readBytes(header.getLength()));
        if (header.getSignatureType() != 0) {
            file.setSignature(reader.readBytes(256));
        }
        file.setBody(body(file));
        file.buildChunkMap();
        file.buildBundleMap();
        return file;
    }

    private static RMANFileBody body(RMANFile file) {
        byte[] uncompressed = Zstd.decompress(file.getCompressedBody(), file.getHeader().getDecompressedLength());
        RandomAccessReader reader = new RandomAccessReader(uncompressed);
        RMANFileBody body = new RMANFileBody();
        body.setHeaderOffset(reader.readInt());
        reader.seek(body.getHeaderOffset());

        RMANFileBodyHeader header = new RMANFileBodyHeader();
        header.setTableOffset(reader.readInt());

        header.setBundleListOffset(reader.position() + reader.readInt());
        header.setLanguageListOffset(reader.position() + reader.readInt());
        header.setFileListOffset(reader.position() + reader.readInt());
        header.setFolderListOffset(reader.position() + reader.readInt());
        header.setKeyHeaderOffset(reader.position() + reader.readInt());
        header.setUnknownOffset(reader.position() + reader.readInt());

        body.setHeader(header);
        body.setBundles(bundle(reader, header));
        body.setLanguages(languages(reader, header));
        body.setFiles(files(reader, header));
        body.setDirectories(directories(reader, header));

        return body;
    }

    public static String getString(RandomAccessReader reader, int startPos, short offset) {
        if (offset == 0) {
            return "";
        } else {
            int offsetPos = startPos + offset;
            reader.seek(offsetPos);

            int stringOffset = reader.readInt();
            reader.seek(offsetPos + stringOffset);
            return reader.readString(reader.readInt());
        }
    }

    public static int get4(RandomAccessReader reader, int startPos, short offset) {
        if (offset == 0) {
            return 0;
        } else {
            int offsetPos = startPos + offset;
            reader.seek(offsetPos);

            return reader.readInt();
        }
    }

    public static long get8(RandomAccessReader reader, int startPos, short offset) {
        if (offset == 0) {
            return 0L;
        } else {
            int offsetPos = startPos + offset;
            reader.seek(offsetPos);

            return reader.readLong();
        }
    }

    public static int getOffset4(RandomAccessReader reader, int startPos, short offset) {
        if (offset == 0) {
            return 0;
        } else {
            int offsetPos = startPos + offset;
            reader.seek(offsetPos);

            return offsetPos + reader.readInt();
        }
    }

    public static List<Long> getList8(RandomAccessReader reader, int startPos, short offset) {
        if (offset == 0) {
            return new ArrayList<>();
        } else {
            int offsetPos = startPos + offset;
            reader.seek(offsetPos);

            int listOffset = reader.readInt();
            reader.seek(offsetPos + listOffset);
            int size = reader.readInt();
            List<Long> values = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                values.add(reader.readLong());
            }

            return values;
        }
    }

    public static short[] getVTableOffsets(RandomAccessReader reader, int startPos) {
        int vTableOffset = startPos - reader.readInt();
        reader.seek(vTableOffset);

        short vtableSize = reader.readShort();
        short vtableEntryCount = reader.readShort();
        short[] vTableEntryOffsets = new short[vtableEntryCount];
        for (int j = 0; j < vtableEntryCount; j++) {
            vTableEntryOffsets[j] = reader.readShort();
        }

        return vTableEntryOffsets;
    }

    private static List<RMANFileBodyFile> files(RandomAccessReader reader, RMANFileBodyHeader header) {
        List<RMANFileBodyFile> files = new ArrayList<>();
        reader.seek(header.getFileListOffset());

        int count = reader.readInt();
        for (int i = 0; i < count; i++) {
            RMANFileBodyFile file = new RMANFileBodyFile();

            int current = reader.position();
            int offset = reader.readInt();
            reader.seek(current + offset);

            int startPos = reader.position();
            short[] vTableEntryOffsets = getVTableOffsets(reader, startPos);

            file.setFileId(get8(reader, startPos, vTableEntryOffsets[0]));
            file.setDirectoryId(get8(reader, startPos, vTableEntryOffsets[1]));
            file.setFileSize(get4(reader, startPos, vTableEntryOffsets[2]));
            file.setName(getString(reader, startPos, vTableEntryOffsets[3]));
            // NOTE: languageId is int in the class def, but in the vtable schema
            // it was marked as 8 (long). I read it as a long and cast it to int for now.
            file.setLanguageId((int) get8(reader, startPos, vTableEntryOffsets[4]));
            reader.seek(startPos + vTableEntryOffsets[5]);
            reader.seek(startPos + vTableEntryOffsets[6]);
            file.setChunkIds(getList8(reader, startPos, vTableEntryOffsets[7]));
            reader.seek(startPos + vTableEntryOffsets[8]);
            file.setSymlink(getString(reader, startPos, vTableEntryOffsets[9]));
            reader.seek(startPos + vTableEntryOffsets[10]);
            reader.seek(startPos + vTableEntryOffsets[11]);
            reader.seek(startPos + vTableEntryOffsets[12]);

            files.add(file);

            reader.seek(current + 4);
        }

        return files;
    }

    private static List<RMANFileBodyDirectory> directories(RandomAccessReader reader, RMANFileBodyHeader header) {
        List<RMANFileBodyDirectory> directories = new ArrayList<>();
        reader.seek(header.getFolderListOffset());

        int count = reader.readInt();
        for (int i = 0; i < count; i++) {
            RMANFileBodyDirectory dir = new RMANFileBodyDirectory();

            int current = reader.position();
            int offset = reader.readInt();
            reader.seek(current + offset);

            int startPos = reader.position();
            short[] vTableEntryOffsets = getVTableOffsets(reader, startPos);

            dir.setDirectoryId(get8(reader, startPos, vTableEntryOffsets[0]));
            dir.setParentId(get8(reader, startPos, vTableEntryOffsets[1]));
            dir.setName(getString(reader, startPos, vTableEntryOffsets[2]));

            directories.add(dir);

            reader.seek(current + 4);
        }

        return directories;
    }

    private static List<RMANFileBodyLanguage> languages(RandomAccessReader reader, RMANFileBodyHeader header) {
        List<RMANFileBodyLanguage> languages = new ArrayList<>();
        reader.seek(header.getLanguageListOffset());

        int count = reader.readInt();
        for (int i = 0; i < count; i++) {
            RMANFileBodyLanguage language = new RMANFileBodyLanguage();

            int current = reader.position();
            int offset = reader.readInt();
            reader.seek(current + offset);

            int startPos = reader.position();
            short[] vTableEntryOffsets = getVTableOffsets(reader, startPos);

            language.setId(get4(reader, startPos, vTableEntryOffsets[0]));
            language.setName(getString(reader, startPos, vTableEntryOffsets[1]));

            languages.add(language);

            reader.seek(current + 4);
        }

        return languages;
    }

    private static List<RMANFileBodyBundle> bundle(RandomAccessReader reader, RMANFileBodyHeader header) {
        List<RMANFileBodyBundle> bundles = new ArrayList<>();
        reader.seek(header.getBundleListOffset());

        int count = reader.readInt();
        for (int i = 0; i < count; i++) {
            RMANFileBodyBundle bundle = new RMANFileBodyBundle();

            int current = reader.position();
            int offset = reader.readInt();
            reader.seek(current + offset);

            int startPos = reader.position();
            short[] vTableEntryOffsets = getVTableOffsets(reader, startPos);

            bundle.setBundleId(get8(reader, startPos, vTableEntryOffsets[0]));
            int chunks_offset = getOffset4(reader, startPos, vTableEntryOffsets[1]);
            reader.seek(chunks_offset);

            List<RMANFileBodyBundleChunk> chunks = new ArrayList<>();
            int chunk_count = reader.readInt();
            for (int j = 0; j < chunk_count; j++) {
                int chunkCurrent = reader.position();
                int chunkOffset = reader.readInt();
                reader.seek(chunkCurrent + chunkOffset);

                int chunkStartPos = reader.position();
                short[] chunkVTableEntryOffsets = getVTableOffsets(reader, chunkStartPos);

                RMANFileBodyBundleChunk chunk = new RMANFileBodyBundleChunk();
                chunk.setChunkId(get8(reader, chunkStartPos, chunkVTableEntryOffsets[0]));
                chunk.setCompressedSize(get4(reader, chunkStartPos, chunkVTableEntryOffsets[1]));
                chunk.setUncompressedSize(get4(reader, chunkStartPos, chunkVTableEntryOffsets[2]));

                chunks.add(chunk);

                reader.seek(chunkCurrent + 4);
            }

            bundle.setChunks(chunks);
            bundles.add(bundle);

            reader.seek(current + 4);
        }

        return bundles;
    }

    private static RMANFileHeader header(RandomAccessReader reader) {
        RMANFileHeader header = new RMANFileHeader();
        header.setMagic(reader.readString(4));
        header.setMajor(reader.readByte());
        header.setMinor(reader.readByte());
        header.setUnknown(reader.readByte());
        header.setSignatureType(reader.readByte());
        header.setOffset(reader.readInt());
        header.setLength(reader.readInt());
        header.setManifestId(reader.readLong());
        header.setDecompressedLength(reader.readInt());
        return header;
    }
}