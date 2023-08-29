package com.hawolt.rman.body;

import com.hawolt.generic.util.RandomAccessReader;

import java.util.*;
import java.util.Map.Entry;

public class RMANVTable {
    public static Map<String, Object> parseVTable(RandomAccessReader reader, Map<String, String> tableDefinition) {
        int startPos = reader.position();

        int vTableOffset = startPos - reader.readInt();
        reader.seek(vTableOffset);

        short vtableSize = reader.readShort();
        short vtableEntryCount = reader.readShort();

        short[] vTableEntryOffsets = new short[vtableEntryCount];
        for (int i = 0; i < vtableEntryCount; i++) {
            vTableEntryOffsets[i] = reader.readShort();
        }

        List<Entry<String, String>> tableEntries = new ArrayList<>(tableDefinition.entrySet());
        Map<String, Object> data = new HashMap<>(tableEntries.size());
        for (int i = 0; i < tableEntries.size(); i++) {
            Entry<String, String> entry = tableEntries.get(i);
            short offset = vTableEntryOffsets[i];

            if (offset == 0) {
                switch (entry.getValue()) {
                    case "0" -> {
                    }
                    case "2" -> data.put(entry.getKey(), (short) 0);
                    case "4" -> data.put(entry.getKey(), 0);
                    case "8" -> data.put(entry.getKey(), 0L);
                    case "offset4" -> data.put(entry.getKey(), 0);
                    case "list8" -> data.put(entry.getKey(), new ArrayList<>());
                    case "string" -> data.put(entry.getKey(), "");
                    default ->
                            throw new RuntimeException("Unhandled read size: " + entry.getKey() + ": " + entry.getValue());
                }
                continue;
            }

            int offsetPos = startPos + offset;
            reader.seek(offsetPos);

            switch (entry.getValue()) {
                case "0" -> {
                }
                case "2" -> data.put(entry.getKey(), reader.readShort());
                case "4" -> data.put(entry.getKey(), reader.readInt());
                case "8" -> data.put(entry.getKey(), reader.readLong());
                case "offset4" -> data.put(entry.getKey(), offsetPos + reader.readInt());
                case "list8" -> {
                    int listOffset = reader.readInt();
                    reader.seek(offsetPos + listOffset);
                    int size = reader.readInt();
                    List<Long> values = new LinkedList<>();
                    for (int j = 0; j < size; j++) {
                        values.add(reader.readLong());
                    }
                    data.put(entry.getKey(), values);
                }
                case "string" -> {
                    int stringOffset = reader.readInt();
                    reader.seek(offsetPos + stringOffset);
                    data.put(entry.getKey(), reader.readString(reader.readInt()));
                }
                default ->
                        throw new RuntimeException("Unhandled read size: " + entry.getKey() + ": " + entry.getValue());
            }
        }

        return data;
    }

    public static Map<String, String> getBundleFields() {
        return new LinkedHashMap<>() {
            {
                put("bundle_id", "8");
                put("chunks_offset", "offset4");
            }
        };
    }

    public static Map<String, String> getChunkFields() {
        return new LinkedHashMap<>() {
            {
                put("chunk_id", "8");
                put("compressed_size", "4");
                put("uncompressed_size", "4");
            }
        };
    }

    public static Map<String, String> getDirectoryFields() {
        return new LinkedHashMap<>() {
            {
                put("directory_id", "8");
                put("parent_id", "8");
                put("name", "string");
            }
        };
    }

    public static Map<String, String> getLanguageFields() {
        return new LinkedHashMap<>() {
            {
                put("language_id", "4");
                put("name", "string");
            }
        };
    }

    public static Map<String, String> getFileFields() {
        return new LinkedHashMap<>() {
            {
                put("file_id", "8");
                put("directory_id", "8");
                put("file_size", "4");
                put("name", "string");
                put("language_id", "8");
                put("unknown", "0");
                put("unknown1", "0");
                put("chunks", "list8");
                put("unknown2", "0");
                put("symlink", "string");
                put("unknown3", "0");
                put("unknown4", "0");
                put("unknown5", "0");
            }
        };
    }
}
