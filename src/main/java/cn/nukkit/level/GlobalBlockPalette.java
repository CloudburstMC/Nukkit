package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import com.google.common.io.ByteStreams;
import cn.nukkit.utils.Binary;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
            if (!state.contains("meta")) continue;

            int id = state.getShort("id");
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
}
