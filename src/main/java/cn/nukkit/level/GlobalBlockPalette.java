package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
<<<<<<< HEAD
import java.util.HashMap;
import java.util.Map;
=======
>>>>>>> 89f8e5c34e0561b09d44e1b85edea7982a24e7a7
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobalBlockPalette {
    private static final Int2IntMap legacyToRuntimeId = new Int2IntOpenHashMap();
    private static final Int2IntMap runtimeIdToLegacy = new Int2IntOpenHashMap();
    private static final Int2ObjectMap<String> legacyIdToString = new Int2ObjectOpenHashMap<>();
    private static final Map<String, Integer> stringToLegacyId = new HashMap<>();
    private static final AtomicInteger runtimeIdAllocator = new AtomicInteger(0);
    public static final byte[] BLOCK_PALETTE;

    static {
        legacyToRuntimeId.defaultReturnValue(-1);
        runtimeIdToLegacy.defaultReturnValue(-1);

        InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states.dat");
        if (stream == null) {
            throw new AssertionError("Unable to locate block state nbt");
<<<<<<< HEAD
        }
        CompoundTag tag;
        try {
            tag = NBTIO.read(stream);
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        ListTag<CompoundTag> states = tag.getList("Palette", CompoundTag.class);
        for (CompoundTag state : states.getAll()) {
            int id = state.getShort("id");
            int meta = state.getShort("meta");
            String name = state.getCompound("block").getString("name");

            registerMapping(id << 4 | meta, name);
            state.remove("meta"); // No point in sending this since the client doesn't use it.
        }
        try {
            BLOCK_PALETTE = NBTIO.write(tag.getList("Palette"), ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
=======
        }
        CompoundTag tag;
        try {
            tag = NBTIO.read(stream);
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        ListTag<CompoundTag> states = tag.getList("Palette", CompoundTag.class);
        for (CompoundTag state : states.getAll()) {
            int id = state.getShort("id");
            int meta = state.getShort("meta");
            registerMapping(id << 4 | meta);
            state.remove("meta"); // No point in sending this since the client doesn't use it.
        }
        try {
            BLOCK_PALETTE = NBTIO.write(tag.getList("Palette"), ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
>>>>>>> 89f8e5c34e0561b09d44e1b85edea7982a24e7a7
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
<<<<<<< HEAD

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
=======
>>>>>>> 89f8e5c34e0561b09d44e1b85edea7982a24e7a7
}
