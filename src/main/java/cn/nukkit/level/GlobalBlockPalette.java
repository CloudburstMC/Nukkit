package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.utils.Binary;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobalBlockPalette {
    private static final Int2IntMap legacyToRuntimeId = new Int2IntOpenHashMap(4096);
    private static final Int2IntMap runtimeIdToLegacy = new Int2IntOpenHashMap(4096);
    private static final HashBiMap<String, Integer> nameToLegacyId = HashBiMap.create();
    private static final AtomicInteger runtimeIdAllocator = new AtomicInteger(0);
    private static final ByteBuf compiledPalette;

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

        ByteBuf buffer = Unpooled.directBuffer();
        Binary.writeUnsignedVarInt(buffer, entries.size());

        for (TableEntry entry : entries) {
            registerMapping((entry.id << 4) | entry.data);
            Binary.writeString(buffer, entry.name);
            buffer.writeShortLE(entry.data);
            buffer.writeShortLE(entry.id);

            nameToLegacyId.putIfAbsent(entry.name, entry.id);
        }

        compiledPalette = buffer;
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

    private static void registerMapping(int legacyId) {
        int runtimeId = runtimeIdAllocator.getAndIncrement();
        runtimeIdToLegacy.put(runtimeId, legacyId);
        legacyToRuntimeId.put(legacyId, runtimeId);
    }

    public static int getLegacyId(int runtime) {
        int legacyId = runtimeIdToLegacy.get(runtime);
        if (legacyId == -1) {
            throw new IllegalArgumentException("Runtime ID: " + runtime + " is not registered");
        }
        return legacyId;
    }

    public static ByteBuf getCompiledPalette() {
        return compiledPalette.duplicate();
    }

    public static int getLegacyIdFromName(String name) {
        //noinspection ConstantConditions
        return nameToLegacyId.get(name);
    }

    public static String getNameFromLegacyId(int id) {
        return nameToLegacyId.inverse().get(id);
    }

    private static class TableEntry {
        private int id;
        private int data;
        private String name;
    }
}
