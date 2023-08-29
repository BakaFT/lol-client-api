package com.hawolt.rman;

import com.github.luben.zstd.Zstd;
import com.hawolt.generic.util.RandomAccessReader;
import com.hawolt.rman.body.*;
import com.hawolt.rman.header.RMANFileHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created: 01/01/2023 03:07
 * Author: Twitter @hawolt
 **/

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

    private static List<RMANFileBodyFile> files(RandomAccessReader reader, RMANFileBodyHeader header) {
        List<RMANFileBodyFile> files = new ArrayList<>();
        reader.seek(header.getFileListOffset());
        List<Map<String, Object>> unmappedDirectories = RMANOffsetTable.parseOffsetTable(reader, RMANVTable.getFileFields(), RMANVTable::parseVTable);
        for (Map<String, Object> entry : unmappedDirectories) {
            RMANFileBodyFile file = new RMANFileBodyFile();
            file.setFileId((Long) entry.get("file_id"));
            file.setDirectoryId((Long) entry.get("directory_id"));
            file.setFileSize((Integer) entry.get("file_size"));
            file.setName((String) entry.get("name"));
            file.setSymlink((String) entry.get("symlink"));
            file.setChunkIds((List<Long>) entry.get("chunks"));
            files.add(file);
        }
        return files;
    }

    private static List<RMANFileBodyDirectory> directories(RandomAccessReader reader, RMANFileBodyHeader header) {
        List<RMANFileBodyDirectory> directories = new ArrayList<>();
        reader.seek(header.getFolderListOffset());
        List<Map<String, Object>> unmappedDirectories = RMANOffsetTable.parseOffsetTable(reader, RMANVTable.getDirectoryFields(), RMANVTable::parseVTable);
        for (Map<String, Object> entry : unmappedDirectories) {
            RMANFileBodyDirectory dir = new RMANFileBodyDirectory();
            dir.setDirectoryId((Long) entry.get("directory_id"));
            dir.setParentId((Long) entry.get("parent_id"));
            dir.setName((String) entry.get("name"));
            directories.add(dir);
        }
        return directories;
    }

    private static List<RMANFileBodyLanguage> languages(RandomAccessReader reader, RMANFileBodyHeader header) {
        List<RMANFileBodyLanguage> languages = new ArrayList<>();
        reader.seek(header.getLanguageListOffset());
        List<Map<String, Object>> unmappedLanguages = RMANOffsetTable.parseOffsetTable(reader, RMANVTable.getLanguageFields(), RMANVTable::parseVTable);
        for (Map<String, Object> entry : unmappedLanguages) {
            RMANFileBodyLanguage dir = new RMANFileBodyLanguage();
            dir.setId((Integer) entry.get("language_id"));
            dir.setName((String) entry.get("name"));
            languages.add(dir);
        }
        return languages;
    }

    private static List<RMANFileBodyBundle> bundle(RandomAccessReader reader, RMANFileBodyHeader header) {
        List<RMANFileBodyBundle> bundles = new ArrayList<>();
        reader.seek(header.getBundleListOffset());
        List<Map<String, Object>> unmappedBundles = RMANOffsetTable.parseOffsetTable(reader, RMANVTable.getBundleFields(), RMANVTable::parseVTable);
        for (Map<String, Object> entry : unmappedBundles) {
            RMANFileBodyBundle bundle = new RMANFileBodyBundle();
            bundle.setBundleId((Long) entry.get("bundle_id"));
            List<RMANFileBodyBundleChunk> chunks = new ArrayList<>();
            reader.seek((Integer) entry.get("chunks_offset"));
            List<Map<String, Object>> unmappedChunks = RMANOffsetTable.parseOffsetTable(reader, RMANVTable.getChunkFields(), RMANVTable::parseVTable);
            for (Map<String, Object> chunkInfo : unmappedChunks) {
                RMANFileBodyBundleChunk chunk = new RMANFileBodyBundleChunk();
                chunk.setCompressedSize((Integer) chunkInfo.get("compressed_size"));
                chunk.setUncompressedSize((Integer) chunkInfo.get("uncompressed_size"));
                chunk.setChunkId((Long) chunkInfo.get("chunk_id"));
                chunks.add(chunk);
            }
            bundle.setChunks(chunks);
            bundles.add(bundle);
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
;