package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import com.google.common.collect.HashBiMap;
import com.google.common.io.ByteStreams;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobalBlockPalette {
    private static final Int2IntMap legacyToRuntimeId = new Int2IntOpenHashMap();
    private static final Int2IntMap runtimeIdToLegacy = new Int2IntOpenHashMap();
    private static final AtomicInteger runtimeIdAllocator = new AtomicInteger(0);
    private static final HashBiMap<String, Integer> nameToLegacyId = HashBiMap.create();
    public static final byte[] BLOCK_PALETTE;

    static {
        legacyToRuntimeId.defaultReturnValue(-1);
        runtimeIdToLegacy.defaultReturnValue(-1);

        InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states.dat");
        if (stream == null) {
            throw new AssertionError("Unable to locate block state nbt");
        }
        ListTag<CompoundTag> tag;
        try {
            //noinspection UnstableApiUsage
            BLOCK_PALETTE = ByteStreams.toByteArray(stream);
            //noinspection unchecked
            tag = (ListTag<CompoundTag>) NBTIO.readNetwork(new ByteArrayInputStream(BLOCK_PALETTE));
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        for (CompoundTag state : tag.getAll()) {
            int runtimeId = runtimeIdAllocator.getAndIncrement();

            int id = state.getShort("id");
            String name = state.getCompound("block").getString("name");
            nameToLegacyId.putIfAbsent(name, id);

            if (!state.contains("meta")) continue;

            int[] meta = state.getIntArray("meta");

            // Resolve to first legacy id
            runtimeIdToLegacy.put(runtimeId, id << 6 | meta[0]);
            for (int val : meta) {
                int legacyId = id << 6 | val;
                legacyToRuntimeId.put(legacyId, runtimeId);
            }
            state.remove("meta"); // No point in sending this since the client doesn't use it.
        }
    }

    public static int getOrCreateRuntimeId(int id, int meta) {
        int legacyId = id << 6 | meta;
        int runtimeId = legacyToRuntimeId.get(legacyId);
        if (runtimeId == -1) {
            //runtimeId = registerMapping(runtimeIdAllocator.incrementAndGet(), legacyId);
            throw new NoSuchElementException("Unmapped block registered id:" + id + " meta:" + meta);
        }
        return runtimeId;
    }

    public static int getOrCreateRuntimeId(int legacyId) throws NoSuchElementException {
        return getOrCreateRuntimeId(legacyId >> 4, legacyId & 0xf);
    }

    public static int getLegacyId(int runtimeId) {
        int legacyId = runtimeIdToLegacy.get(runtimeId);
        if (legacyId == -1) {
            return 0;
        }
        return ((legacyId >> 6) << 4) | legacyId & 0xf;
    }

    public static int getLegacyIdFromName(String name) {
        //noinspection ConstantConditions
        return nameToLegacyId.get(name);
    }

    public static String getNameFromLegacyId(int id) {
        return nameToLegacyId.inverse().get(id);
    }
}
