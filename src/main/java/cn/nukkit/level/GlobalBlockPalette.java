package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.utils.BinaryStream;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobalBlockPalette {
    private static final Int2IntMap legacyToRuntimeId = new Int2IntOpenHashMap();
    private static final Int2IntMap runtimeIdToLegacy = new Int2IntOpenHashMap();
    private static final Int2ObjectMap<String> legacyIdToString = new Int2ObjectOpenHashMap<>();
    private static final Map<String, Integer> stringToLegacyId = new HashMap<>();
    private static final AtomicInteger runtimeIdAllocator = new AtomicInteger(0);
    private static final byte[] compiledPalette;

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
            table.putLShort(entry.id);
        }

        compiledPalette = table.getBuffer();
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

    public static int getLegacyId(int runtimeId) {
        return runtimeIdToLegacy.get(runtimeId);
    }

    public static String getName(int id, int meta) {
        return legacyIdToString.get((id << 4) | meta);
    }

    private static int registerMapping(int legacyId, String name) {
        int runtimeId = runtimeIdAllocator.getAndIncrement();
        runtimeIdToLegacy.put(runtimeId, legacyId);
        legacyIdToString.put(legacyId, name);
        stringToLegacyId.put(name, legacyId);
        legacyToRuntimeId.put(legacyId, runtimeId);
        return runtimeId;
    }

    public static byte[] getCompiledPalette() {
        return compiledPalette;
    }

    private static class TableEntry {
        private int id;
        private int data;
        private String name;
    }
}
