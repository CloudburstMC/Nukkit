package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.utils.MainLogger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobalBlockPalette {
    private static final Int2IntArrayMap legacyToRuntimeId = new Int2IntArrayMap();
    private static final Int2IntArrayMap runtimeIdToLegacy = new Int2IntArrayMap();
    private static final AtomicInteger runtimeIdAllocator = new AtomicInteger(0);

    static {
        legacyToRuntimeId.defaultReturnValue(-1);
        runtimeIdToLegacy.defaultReturnValue(-1);

        try {
            Gson gson = new Gson();
            Reader reader = new InputStreamReader(Server.class.getClassLoader().getResourceAsStream("runtimeid_table.json"), "UTF-8");
            Type collectionType = new TypeToken<Collection<TableEntry>>(){}.getType();
            Collection<TableEntry> entries = gson.fromJson(reader, collectionType);

            for (TableEntry entry : entries) {
                registerMapping(entry.runtimeID, (entry.id << 4) | entry.data);
            }

        } catch (IOException e) {
            MainLogger.getLogger().logException(e);
        }
    }

    public static int getOrCreateRuntimeId(int id, int meta) {
        return getOrCreateRuntimeId((id << 4) | meta);
    }

    public static int getOrCreateRuntimeId(int legacyId) {
        int runtimeId = legacyToRuntimeId.get(legacyId);
        if (runtimeId == -1) {
            runtimeId = registerMapping(runtimeIdAllocator.incrementAndGet(), legacyId);
            MainLogger.getLogger().warning("Unmapped block registered. May not be recognised client-side");
        }
        return runtimeId;
    }

    private static int registerMapping(int runtimeId, int legacyId) {
        runtimeIdToLegacy.put(runtimeId, legacyId);
        legacyToRuntimeId.put(legacyId, runtimeId);
        runtimeIdAllocator.set(Math.max(runtimeIdAllocator.get(), runtimeId));
        return runtimeId;
    }

    private static class TableEntry {
        private int id;
        private int data;
        private int runtimeID;
        private String name;
    }
}
