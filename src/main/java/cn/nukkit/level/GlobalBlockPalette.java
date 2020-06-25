package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import it.unimi.dsi.fastutil.ints.*;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class GlobalBlockPalette {
    private static final Int2IntMap legacyToRuntimeId = new Int2IntOpenHashMap();
    private static final Int2IntMap runtimeIdToLegacy = new Int2IntOpenHashMap();
    private static final Int2ObjectMap<String> legacyIdToString = new Int2ObjectOpenHashMap<>();
    private static final Map<String, Integer> stringToLegacyId = new HashMap<>();
    private static final AtomicInteger runtimeIdAllocator = new AtomicInteger(0);
    public static final byte[] BLOCK_PALETTE;
    private static final Int2ObjectMap<IntSet> unknownRuntimeIds = new Int2ObjectOpenHashMap<>();

    static {
        legacyToRuntimeId.defaultReturnValue(-1);
        runtimeIdToLegacy.defaultReturnValue(-1);

        Map<CompoundTag, List<CompoundTag>> metaOverrides = new LinkedHashMap<>();
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states_overrides.dat")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }

            ListTag<CompoundTag> states;
            try (BufferedInputStream buffered = new BufferedInputStream(stream)) {
                states = NBTIO.read(buffered).getList("Overrides", CompoundTag.class);
            }

            for (CompoundTag override : states.getAll()) {
                if (override.contains("block") && override.contains("LegacyStates")) {
                    metaOverrides.put(override.getCompound("block").remove("version"), override.getList("LegacyStates", CompoundTag.class).getAll());
                }
            }

        } catch (IOException e) {
            throw new AssertionError(e);
        }

        ListTag<CompoundTag> tag;
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states.dat")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }

            try(BufferedInputStream buffered = new BufferedInputStream(stream)) {
                //noinspection unchecked
                tag = (ListTag<CompoundTag>) NBTIO.readTag(buffered, ByteOrder.LITTLE_ENDIAN, false);
            }

        } catch (IOException e) {
            throw new AssertionError(e);
        }

        for (CompoundTag state : tag.getAll()) {
            int runtimeId = runtimeIdAllocator.getAndIncrement();
            List<CompoundTag> legacyStates = metaOverrides.get(state.getCompound("block").copy().remove("version"));
            if (legacyStates == null) {
                if (!state.contains("LegacyStates")) {
                    continue;
                } else {
                    legacyStates = state.getList("LegacyStates", CompoundTag.class).getAll();
                }
            }

            String name = state.getCompound("block").getString("name");

            // Resolve to first legacy id
            CompoundTag firstState = legacyStates.get(0);
            int id = firstState.getInt("id");
            runtimeIdToLegacy.put(runtimeId, id << 6 | firstState.getShort("val"));
            stringToLegacyId.put(name, id);
            legacyIdToString.put(id, name);

            for (CompoundTag legacyState : legacyStates) {
                int legacyId = legacyState.getInt("id") << 6 | legacyState.getShort("val");
                legacyToRuntimeId.put(legacyId, runtimeId);
            }
            // No point in sending this since the client doesn't use it.
            state.remove("meta");
            state.remove("LegacyStates");
        }

        try {
            BLOCK_PALETTE = NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static int getOrCreateRuntimeId(int id, int meta) {
        // Special case for PN-96 PowerNukkit#210 where the world contains blocks like 0:13, 0:7, etc
        if (id == 0) meta = 0;
        
        int legacyId = id << 6 | meta;
        int runtimeId = legacyToRuntimeId.get(legacyId);
        if (runtimeId == -1) {
            runtimeId = legacyToRuntimeId.get(248 << 6);
            if (unknownRuntimeIds.computeIfAbsent(id, k -> new IntOpenHashSet(5)).add(meta)) {
                log.error("Found an unknown BlockId:Meta combination: "+id+":"+meta+", replacing with an \"UPDATE!\" block.");
            }
        }
        return runtimeId;
    }

    public static int getOrCreateRuntimeId(int legacyId) throws NoSuchElementException {
        return getOrCreateRuntimeId(legacyId >> Block.DATA_BITS, legacyId & Block.DATA_MASK);
    }

    public static int getLegacyId(int runtimeId) {
        return runtimeIdToLegacy.get(runtimeId);
    }

    public static String getName(int id) {
        return legacyIdToString.get(id);
    }
}
