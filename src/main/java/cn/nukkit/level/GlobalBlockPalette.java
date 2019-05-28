package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.utils.BinaryStream;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobalBlockPalette {
    private static final Int2IntArrayMap legacyToRuntimeId = new Int2IntArrayMap();
    private static final Int2IntArrayMap runtimeIdToLegacy = new Int2IntArrayMap();
    private static final HashBiMap<Integer, String> idToName = HashBiMap.create();
    private static final AtomicInteger runtimeIdAllocator = new AtomicInteger(0);
    private static final byte[] compiledTable;

    static {
        legacyToRuntimeId.defaultReturnValue(-1);
        runtimeIdToLegacy.defaultReturnValue(-1);

        InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtimeid_table.json");
        if (stream == null) {
            throw new AssertionError("Unable to locate RuntimeID table");
        }
        Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<TableEntry>>() {
        }.getType();
        Collection<TableEntry> entries = gson.fromJson(reader, collectionType);
        BinaryStream table = new BinaryStream();

        table.putUnsignedVarInt(entries.size());

        for (TableEntry entry : entries) {
            registerMapping((entry.id << 4) | entry.data, entry.name);
            table.putString(entry.name);
            table.putLShort(entry.data);
        }

        compiledTable = table.getBuffer();
    }

    public static int getOrCreateRuntimeId(int id, int meta) {
        return getOrCreateRuntimeId((id << 4) | meta);
    }

    public static int getOrCreateRuntimeId(int legacyId) throws NoSuchElementException {
        int runtimeId = legacyToRuntimeId.get(legacyId);
        if (runtimeId == -1) {
            //runtimeId = registerMapping(runtimeIdAllocator.incrementAndGet(), legacyId);
            throw new NoSuchElementException("Unmapped block registered id:" + (legacyId >>> 4) + " meta:" + (legacyId & 0xf));
        }
        return runtimeId;
    }

    public static String getName(int id) throws NoSuchElementException {
        String name = idToName.get(id);
        if (name == null) {
            throw new NoSuchElementException("Unmapped block registered id: " + id);
        }
        return name;
    }

    public static int getId(String name) throws NoSuchElementException {
        Integer id = idToName.inverse().get(name);
        if (id == null) {
            throw new NoSuchElementException("Unmapped block registered name: " + name);
        }
        return id;
    }

    private static int registerMapping(int legacyId, String name) {
        int runtimeId = runtimeIdAllocator.getAndIncrement();
        runtimeIdToLegacy.put(runtimeId, legacyId);
        legacyToRuntimeId.put(legacyId, runtimeId);
        idToName.putIfAbsent(legacyId >>> 4, name);
        return runtimeId;
    }

    public static byte[] getCompiledTable() {
        return compiledTable;
    }

    private static class TableEntry {
        private int id;
        private int data;
        private String name;
    }
}
